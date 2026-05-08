package com.example.j2n.room_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.room_srv.constant.MessageEnum;
import com.example.j2n.room_srv.controller.request.BillRequest;
import com.example.j2n.room_srv.messaging.room.event.RoomBillSyncedEvent;
import com.example.j2n.room_srv.messaging.room.publisher.RoomEventPublisher;
import com.example.j2n.room_srv.repository.BillRepository;
import com.example.j2n.room_srv.repository.RoomRepository;
import com.example.j2n.room_srv.repository.entity.BillEntity;
import com.example.j2n.room_srv.repository.entity.RoomEntity;
import com.example.j2n.room_srv.repository.entity.UtilityConfigEntity;
import com.example.j2n.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BillingService {

    private final BillRepository billRepository;
    private final RoomRepository roomRepository;
    private final UtilityConfigService utilityConfigService;
    private final RoomEventPublisher roomEventPublisher;

    @Transactional
    @LogAround(message = "Calculate monthly bill")
    public BaseResponse<BillEntity> calculateBill(BillRequest request) {
        RoomEntity room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.ROOM_NOT_FOUND.withArgs(request.getRoomId())));

        // Get latest meter readings (simplified: assume old index is the new index of previous month)
        // In a real app, you'd query the latest bill for this room
        Integer electricOld = 0; // Should be fetched from previous bill
        
        // Calculate amounts
        BigDecimal totalAmount = room.getBasePrice();
        
        List<UtilityConfigEntity> configs = utilityConfigService.getActiveConfigs();
        for (UtilityConfigEntity config : configs) {
            if ("ELECTRIC".equals(config.getType())) {
                int usage = request.getElectricityUsage() != null ? request.getElectricityUsage() : 0;
                if (usage == 0 && request.getElectricityNewIndex() != null) {
                    usage = request.getElectricityNewIndex() - electricOld;
                }
                totalAmount = totalAmount.add(config.getUnitPrice().multiply(BigDecimal.valueOf(usage)));
            } else if ("WATER".equals(config.getType()) && request.getWaterUsage() != null) {
                totalAmount = totalAmount.add(config.getUnitPrice().multiply(BigDecimal.valueOf(request.getWaterUsage())));
            }
        }

        if (request.getOtherServiceFees() != null) {
            totalAmount = totalAmount.add(request.getOtherServiceFees());
        }

        BillEntity bill = BillEntity.builder()
                .id(UUID.randomUUID().toString())
                .room(room)
                .renterId(request.getRenterId())
                .billingMonth(request.getMonth())
                .electricityOldIndex(electricOld)
                .electricityNewIndex(request.getElectricityNewIndex())
                .electricityUsage(request.getElectricityUsage())
                .waterUsage(request.getWaterUsage())
                .serviceFees(request.getOtherServiceFees())
                .totalAmount(totalAmount)
                .status("UNPAID")
                .build();

        BillEntity savedBill = billRepository.save(bill);

        // SYNC to order-srv
        roomEventPublisher.publishRoomBillSynced(RoomBillSyncedEvent.builder()
                .billId(savedBill.getId())
                .roomNumber(room.getRoomNumber())
                .title("Tiền phòng tháng " + bill.getBillingMonth() + " - P." + room.getRoomNumber())
                .totalAmount(savedBill.getTotalAmount())
                .isPaid(false)
                .build());

        return ResponseFactory.success(savedBill);
    }


    @LogAround(message = "Get bills by room ID")
    public BaseResponse<List<BillEntity>> getBillsByRoom(Long roomId) {
        return ResponseFactory.success(billRepository.findByRoomId(roomId));
    }
}
