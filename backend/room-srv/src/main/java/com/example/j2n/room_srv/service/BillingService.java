package com.example.j2n.room_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.room_srv.constant.BillStatus;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.room_srv.constant.MessageEnum;
import com.example.j2n.room_srv.controller.request.BillRequest;
import com.example.j2n.room_srv.repository.BillRepository;
import com.example.j2n.room_srv.repository.entity.BillEntity;
import com.example.j2n.room_srv.repository.entity.RoomEntity;
import com.example.j2n.room_srv.repository.entity.RoomMemberEntity;
import com.example.j2n.room_srv.repository.entity.FeeEntity;
import com.example.j2n.room_srv.controller.request.SearchBillsRequest;
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
    public BaseResponse<List<BillEntity>> getBillsByRoomId(Long roomId) {
        RoomEntity room = roomService.findRoomByIdOrThrow(roomId);
        roomSecurityUtil.checkRoomAccess(room);
        return ResponseFactory.success(billRepository.findByRoomId(roomId));
    }

    @Transactional
    @LogAround(message = "Calculate monthly bill")
    public BaseResponse<BillEntity> calculateBill(BillRequest request) {
        log.info("Calculating monthly bill for room {}", request.getRoomId());
        Integer reqMonth = request.getMonth() != null ? request.getMonth().orElse(null) : null;
        Integer validMonth = validateMonth(reqMonth, request.getRoomId());
        request.setMonth(Optional.of(validMonth));
        RoomEntity room = roomService.findRoomByIdOrThrow(request.getRoomId());
        roomSecurityUtil.checkRoomAccess(room);
        Integer electricOld = room.getCurrentElectricIndex() != null ? room.getCurrentElectricIndex() : 0;
        BigDecimal totalAmount = calculateTotalAmount(request, room, electricOld);
        BillEntity bill = buildBillEntity(request, room, electricOld, totalAmount);
        return ResponseFactory.success(billRepository.save(bill));
    }

    @Transactional
    @LogAround(message = "Calculate bills for all rooms")
    public BaseResponse<List<BillEntity>> calculateAllBills(Integer month) {
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

            billsToSave.add(buildBillForRoom(room, primaryRenterId, billingMonth));
        }

        if (billsToSave.isEmpty()) {
            return ResponseFactory.success(List.of());
        }

        List<BillEntity> savedBills = billRepository.saveAll(billsToSave);

        // Publish event
        publishBillsCalculatedEvent(savedBills, billingMonth);

        return ResponseFactory.success(savedBills);
    }

    private void publishBillsCalculatedEvent(List<BillEntity> savedBills, Integer month) {
        log.info("Aggregating billing data for event publishing");
        List<FeeEntity> configs = feeService.getActiveFees();
        BigDecimal electricUnitPrice = configs.stream()
                .filter(f -> "ELECTRIC".equals(f.getName()))
                .map(FeeEntity::getUnitPrice)
                .findFirst()
                .orElse(BigDecimal.ZERO);

        BigDecimal waterUnitPrice = configs.stream()
                .filter(f -> "WATER".equals(f.getName()))
                .map(FeeEntity::getUnitPrice)
                .findFirst()
                .orElse(BigDecimal.ZERO);

        BigDecimal totalUnpaidAmount = BigDecimal.ZERO;
        BigDecimal totalElectricityAmount = BigDecimal.ZERO;
        BigDecimal totalWaterAmount = BigDecimal.ZERO;

        for (BillEntity bill : savedBills) {
            totalUnpaidAmount = totalUnpaidAmount.add(bill.getTotalAmount());
            totalElectricityAmount = totalElectricityAmount
                    .add(electricUnitPrice.multiply(BigDecimal.valueOf(bill.getElectricityUsage())));
            totalWaterAmount = totalWaterAmount.add(waterUnitPrice.multiply(BigDecimal.valueOf(bill.getWaterUsage())));
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

    private BillEntity buildBillForRoom(RoomEntity room, Long renterId, Integer month) {
        log.info("Building bill for room {}", room.getRoomNumber());
        Integer validMonth = validateMonth(month, room.getId());
        BillRequest request = BillRequest.builder()
                .roomId(room.getId())
                .renterId(renterId)
                .month(Optional.of(validMonth))
                .build();
        Integer electricOld = room.getCurrentElectricIndex() != null ? room.getCurrentElectricIndex() : 0;
        BigDecimal totalAmount = calculateTotalAmount(request, room, electricOld);
        return buildBillEntity(request, room, electricOld, totalAmount);
    }

    private BigDecimal calculateTotalAmount(BillRequest request, RoomEntity room, Integer electricOld) {
        log.info("Calculating total amount for room {}", room.getRoomNumber());
        BigDecimal totalAmount = room.getBasePrice();
        List<FeeEntity> configs = feeService.getActiveFees();

        int electricUsage = 0;
        if (request.getElectricityNewIndex() != null && request.getElectricityNewIndex() > electricOld) {
            electricUsage = request.getElectricityNewIndex() - electricOld;
        }

        for (FeeEntity config : configs) {
            if ("ELECTRIC".equals(config.getName())) {
                totalAmount = totalAmount.add(config.getUnitPrice().multiply(BigDecimal.valueOf(electricUsage)));
            }
        }

        return totalAmount;
    }

    private BillEntity buildBillEntity(BillRequest request, RoomEntity room, Integer electricOld,
            BigDecimal totalAmount) {
        log.info("Building bill entity for room {}", room.getRoomNumber());
        int electricUsage = 0;
        if (request.getElectricityNewIndex() != null && request.getElectricityNewIndex() > electricOld) {
            electricUsage = request.getElectricityNewIndex() - electricOld;
        }

        return BillEntity.builder()
                .id(UUID.randomUUID().toString())
                .room(room)
                .renterId(request.getRenterId())
                .billingMonth(request.getMonth() != null ? request.getMonth().orElse(null) : null)
                .electricityOldIndex(electricOld)
                .electricityNewIndex(request.getElectricityNewIndex())
                .electricityUsage(electricUsage)
                .waterUsage(0)
                .serviceFees(BigDecimal.ZERO)
                .totalAmount(totalAmount)
                .status(BillStatus.UNPAID)
                .build();
    }

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

    private List<SearchCriteria> buildSearchCriteria(SearchBillsRequest request, RoomEntity room) {
        log.info("Building search criteria for bill search request: {}", request);
        List<SearchCriteria> criteriaList = new ArrayList<>();

        criteriaList.add(SearchCriteria.builder()
                .fieldName("room")
                .value(room)
                .operation(EQUAL)
                .build());

        request.getRenterId().ifPresent(renterId -> criteriaList.add(SearchCriteria.builder()
                .fieldName("renterId")
                .value(renterId)
                .operation(EQUAL)
                .build()));

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
                .renterId(entity.getRenterId())
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
