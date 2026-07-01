package com.example.j2n.room_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.room_srv.constant.BillStatus;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.room_srv.constant.MessageEnum;
import com.example.j2n.room_srv.controller.request.BillRequest;
import com.example.j2n.room_srv.controller.request.CalculateAllBillsRequest;
import com.example.j2n.room_srv.repository.BillRepository;
import com.example.j2n.room_srv.repository.entity.BillEntity;
import com.example.j2n.room_srv.repository.entity.RoomEntity;
import com.example.j2n.room_srv.repository.entity.RoomMemberEntity;
import com.example.j2n.room_srv.repository.entity.FeeEntity;
import com.example.j2n.room_srv.repository.entity.RoomFeeEntity;
import com.example.j2n.room_srv.constant.FeeConstant;
import com.example.j2n.room_srv.controller.request.SearchBillsRequest;
import com.example.j2n.room_srv.controller.request.SearchBillsAdminRequest;
import com.example.j2n.room_srv.service.response.BillResponse;
import com.example.j2n.room_srv.service.response.SearchBillsResponse;
import com.example.j2n.room_srv.messaging.room.event.BillsCalculatedEvent;
import com.example.j2n.room_srv.messaging.room.publisher.RoomEventPublisher;

import com.example.j2n.utils.PageUtil;
import com.example.j2n.utils.ResponseFactory;
import com.example.j2n.utils.SearchFactory;
import com.example.j2n.utils.SearchPredicateBuilder.SearchCriteria;
import com.example.j2n.room_srv.utils.RoomSecurityUtil;
import org.springframework.data.domain.Page;
import static com.example.j2n.utils.SearchPredicateBuilder.SearchOperation.EQUAL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BillingService {

    private final BillRepository billRepository;
    private final RoomService roomService;
    private final FeeService feeService;
    private final SearchFactory searchFactory;
    private final RoomEventPublisher eventPublisher;
    private final RoomSecurityUtil roomSecurityUtil;

    @LogAround(message = "Get bills by room ID")
    public BaseResponse<List<BillResponse>> getBillsByRoomId(Long roomId) {
        RoomEntity room = roomService.findRoomByIdOrThrow(roomId);
        roomSecurityUtil.checkRoomAccess(room);
        List<BillEntity> bills = billRepository.findByRoomId(roomId);
        List<BillResponse> responses = bills.stream().map(this::mapToResponse).toList();
        return ResponseFactory.success(responses);
    }

    @Transactional
    @LogAround(message = "Calculate monthly bill")
    public BaseResponse<BillResponse> calculateBill(BillRequest request) {
        log.info("Calculating monthly bill for room {}", request.getRoomId());
        Integer reqMonth = request.getMonth() != null ? request.getMonth().orElse(null) : null;
        Integer validMonth = validateMonth(reqMonth, request.getRoomId());
        request.setMonth(Optional.of(validMonth));
        RoomEntity room = roomService.findRoomByIdOrThrow(request.getRoomId());
        roomSecurityUtil.checkRoomAccess(room);
        Integer electricOld = room.getCurrentElectricIndex() != null ? room.getCurrentElectricIndex() : 0;
        BigDecimal totalAmount = calculateTotalAmount(request, room, electricOld);
        BillEntity bill = buildBillEntity(request, room, electricOld, totalAmount);
        return ResponseFactory.success(mapToResponse(billRepository.save(bill)));
    }

    @Transactional
    @LogAround(message = "Calculate bills for all rooms")
    public BaseResponse<List<BillResponse>> calculateAllBills(CalculateAllBillsRequest request) {
        Integer month = request.getMonth();
        Map<Long, Integer> electricIndices = request.getElectricIndices();
        log.info("Calculating bills for all rooms for month {}", month);
        Integer billingMonth = month != null ? month : LocalDate.now().getMonthValue();
        List<RoomEntity> rooms = roomService.getAllRooms();
        List<BillEntity> billsToSave = new ArrayList<>();

        for (RoomEntity room : rooms) {
            Long primaryRenterId = findPrimaryRenterId(room);

            if (primaryRenterId == null) {
                log.warn("Room {} has no primary renter, skipping bill calculation", room.getRoomNumber());
                continue;
            }

            Integer newElectricIndex = electricIndices.getOrDefault(room.getId(), null);
            billsToSave.add(buildBillForRoom(room, billingMonth, newElectricIndex));
        }

        if (billsToSave.isEmpty()) {
            return ResponseFactory.success(List.of());
        }

        List<BillEntity> savedBills = billRepository.saveAll(billsToSave);

        // Publish event
        publishBillsCalculatedEvent(savedBills, billingMonth);

        List<BillResponse> responses = savedBills.stream().map(this::mapToResponse).toList();
        return ResponseFactory.success(responses);
    }

    private void publishBillsCalculatedEvent(List<BillEntity> savedBills, Integer month) {
        log.info("Aggregating billing data for event publishing");

        BigDecimal totalUnpaidAmount = BigDecimal.ZERO;
        BigDecimal totalElectricityAmount = BigDecimal.ZERO;
        BigDecimal totalWaterAmount = BigDecimal.ZERO;

        for (BillEntity bill : savedBills) {
            totalUnpaidAmount = totalUnpaidAmount.add(bill.getTotalAmount());
            totalElectricityAmount = totalElectricityAmount.add(BigDecimal.valueOf(bill.getElectricityUsage()));
            totalWaterAmount = totalWaterAmount.add(BigDecimal.valueOf(bill.getWaterUsage()));
        }

        eventPublisher.publishBillsCalculated(BillsCalculatedEvent.builder()
                .totalBills(savedBills.size())
                .totalUnpaidAmount(totalUnpaidAmount)
                .totalElectricityAmount(totalElectricityAmount)
                .totalWaterAmount(totalWaterAmount)
                .monthYear(String.format("%d-%02d", LocalDate.now().getYear(), month))
                .build());
    }

    private Long findPrimaryRenterId(RoomEntity room) {
        return room.getMembers().stream()
                .filter(member -> Boolean.TRUE.equals(member.getIsPrimary()))
                .map(RoomMemberEntity::getUserId)
                .findFirst()
                .orElse(null);
    }

    private BillEntity buildBillForRoom(RoomEntity room, Integer month, Integer newElectricIndex) {
        log.info("Building bill for room {}", room.getRoomNumber());
        Integer validMonth = validateMonth(month, room.getId());
        BillRequest request = BillRequest.builder()
                .roomId(room.getId())
                .month(Optional.of(validMonth))
                .electricityNewIndex(newElectricIndex)
                .build();
        Integer electricOld = room.getCurrentElectricIndex() != null ? room.getCurrentElectricIndex() : 0;
        BigDecimal totalAmount = calculateTotalAmount(request, room, electricOld);
        return buildBillEntity(request, room, electricOld, totalAmount);
    }

    private BigDecimal calculateTotalAmount(BillRequest request, RoomEntity room, Integer electricOld) {
        log.info("Calculating total amount for room {}", room.getRoomNumber());
        BigDecimal totalAmount = room.getBasePrice();

        int electricUsage = calculateElectricUsage(request, electricOld);
        int memberCount = room.getMembers() != null ? room.getMembers().size() : 0;

        if (room.getFees() != null) {
            BigDecimal electricMultiplier = BigDecimal.valueOf(electricUsage);
            BigDecimal memberMultiplier = BigDecimal.valueOf(memberCount);

            for (RoomFeeEntity roomFee : room.getFees()) {
                FeeEntity config = roomFee.getFee();
                if (config == null || Boolean.FALSE.equals(config.getIsActive())) {
                    continue;
                }
                totalAmount = totalAmount.add(calculateSingleFee(config, electricMultiplier, memberMultiplier));
            }
        }

        return totalAmount;
    }

    private BillEntity buildBillEntity(BillRequest request, RoomEntity room, Integer electricOld, BigDecimal totalAmount) {
        log.info("Building bill entity for room {}", room.getRoomNumber());
        int electricUsage = calculateElectricUsage(request, electricOld);
        FeeAmounts feeAmounts = calculateFeeAmounts(room, electricUsage);

        return BillEntity.builder()
                .id(UUID.randomUUID().toString())
                .room(room)
                .billingMonth(request.getMonth() != null ? request.getMonth().orElse(null) : null)
                .electricityOldIndex(electricOld)
                .electricityNewIndex(request.getElectricityNewIndex())
                .electricityUsage(feeAmounts.electricUsageAmount())
                .waterUsage(feeAmounts.waterUsageAmount())
                .serviceFees(feeAmounts.serviceFeesAmount())
                .totalAmount(totalAmount)
                .status(BillStatus.UNPAID)
                .build();
    }

    private int calculateElectricUsage(BillRequest request, Integer electricOld) {
        if (request.getElectricityNewIndex() != null && request.getElectricityNewIndex() > electricOld) {
            return request.getElectricityNewIndex() - electricOld;
        }
        return 0;
    }

    private FeeAmounts calculateFeeAmounts(RoomEntity room, int electricUsage) {
        int electricUsageAmount = 0;
        int waterUsageAmount = 0;
        BigDecimal serviceFeesAmount = BigDecimal.ZERO;

        if (room.getFees() != null) {
            int memberCount = room.getMembers() != null ? room.getMembers().size() : 0;
            BigDecimal electricMultiplier = BigDecimal.valueOf(electricUsage);
            BigDecimal memberMultiplier = BigDecimal.valueOf(memberCount);

            for (RoomFeeEntity roomFee : room.getFees()) {
                FeeEntity config = roomFee.getFee();
                if (config == null || Boolean.FALSE.equals(config.getIsActive())) {
                    continue;
                }

                BigDecimal feeAmount = calculateSingleFee(config, electricMultiplier, memberMultiplier);

                if (FeeConstant.FEE_ELECTRICITY.equalsIgnoreCase(config.getName())) {
                    electricUsageAmount = feeAmount.intValue();
                } else if (FeeConstant.FEE_WATER.equalsIgnoreCase(config.getName())) {
                    waterUsageAmount = feeAmount.intValue();
                } else {
                    serviceFeesAmount = serviceFeesAmount.add(feeAmount);
                }
            }
        }

        return new FeeAmounts(electricUsageAmount, waterUsageAmount, serviceFeesAmount);
    }

    private BigDecimal calculateSingleFee(FeeEntity config, BigDecimal electricMultiplier, BigDecimal memberMultiplier) {
        BigDecimal unitPrice = config.getUnitPrice() != null ? config.getUnitPrice() : BigDecimal.ZERO;

        if (FeeConstant.FEE_ELECTRICITY.equalsIgnoreCase(config.getName())) {
            return unitPrice.multiply(electricMultiplier);
        } else if (FeeConstant.UNIT_PERSON.equalsIgnoreCase(config.getUnitName())) {
            return unitPrice.multiply(memberMultiplier);
        } else {
            return unitPrice;
        }
    }

    private record FeeAmounts(int electricUsageAmount, int waterUsageAmount, BigDecimal serviceFeesAmount) {}

    @LogAround(message = "Search bills")
    public BaseResponse<SearchBillsResponse> searchBills(SearchBillsRequest request) {
        RoomEntity room = roomService.findRoomByIdOrThrow(request.getRoomId());
        List<SearchCriteria> criteriaList = buildSearchCriteria(request, room);
        Page<BillResponse> pageData = searchFactory.searchAndMap(
                billRepository,
                criteriaList,
                request,
                this::mapToResponse);

        SearchBillsResponse response = SearchBillsResponse.builder()
                .bills(pageData.getContent())
                .page(PageUtil.buildPagingMeta(pageData))
                .build();
        return ResponseFactory.success(response);
    }

    @LogAround(message = "Search bills (Admin)")
    public BaseResponse<SearchBillsResponse> searchBillsAdmin(SearchBillsAdminRequest request) {
        List<SearchCriteria> criteriaList = new ArrayList<>();

        if (request.getRoomId() != null && request.getRoomId().isPresent()) {
            RoomEntity room = roomService.findRoomByIdOrThrow(request.getRoomId().get());
            criteriaList.add(SearchCriteria.builder()
                    .fieldName("room")
                    .value(room)
                    .operation(EQUAL)
                    .build());
        }

        request.getBillingMonth().ifPresent(month -> criteriaList.add(SearchCriteria.builder()
                .fieldName("billingMonth")
                .value(month)
                .operation(EQUAL)
                .build()));

        request.getStatus().ifPresent(status -> criteriaList.add(SearchCriteria.builder()
                .fieldName("status")
                .value(status)
                .operation(EQUAL)
                .build()));

        Page<BillResponse> pageData = searchFactory.searchAndMap(
                billRepository,
                criteriaList,
                request,
                this::mapToResponse);

        SearchBillsResponse response = SearchBillsResponse.builder()
                .bills(pageData.getContent())
                .page(PageUtil.buildPagingMeta(pageData))
                .build();
        return ResponseFactory.success(response);
    }

    private List<SearchCriteria> buildSearchCriteria(SearchBillsRequest request, RoomEntity room) {
        log.info("Building search criteria for bill search request: {}", request);
        List<SearchCriteria> criteriaList = new ArrayList<>();

        criteriaList.add(SearchCriteria.builder()
                .fieldName("room")
                .value(room)
                .operation(EQUAL)
                .build());

        request.getBillingMonth().ifPresent(month -> criteriaList.add(SearchCriteria.builder()
                .fieldName("billingMonth")
                .value(month)
                .operation(EQUAL)
                .build()));

        request.getStatus().ifPresent(status -> criteriaList.add(SearchCriteria.builder()
                .fieldName("status")
                .value(status)
                .operation(EQUAL)
                .build()));

        return criteriaList;
    }

    private BillResponse mapToResponse(BillEntity entity) {
        return BillResponse.builder()
                .id(entity.getId())
                .roomId(entity.getRoom() != null ? entity.getRoom().getId() : null)
                .roomNumber(entity.getRoom() != null ? entity.getRoom().getRoomNumber() : null)
                .billingMonth(entity.getBillingMonth())
                .electricityOldIndex(entity.getElectricityOldIndex())
                .electricityNewIndex(entity.getElectricityNewIndex())
                .electricityUsage(entity.getElectricityUsage())
                .waterUsage(entity.getWaterUsage())
                .serviceFees(entity.getServiceFees())
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus())
                .orderId(entity.getOrderId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Integer validateMonth(Integer month, Long roomId) {
        log.info("Validating month {} for room {}", month, roomId);
        int currentMonth = LocalDate.now().getMonthValue();
        Integer targetMonth = (month == null || month <= 0) ? currentMonth : month;

        if (targetMonth > currentMonth) {
            throw new InvalidInputException(MessageEnum.INVALID_MONTH.withArgs(targetMonth));
        }

        List<BillEntity> existingBills = billRepository.findByRoomIdAndBillingMonth(roomId, targetMonth);
        if (!existingBills.isEmpty()) {
            throw new InvalidInputException(MessageEnum.BILL_ALREADY_EXISTS.withArgs(targetMonth));
        }

        return targetMonth;
    }

}
