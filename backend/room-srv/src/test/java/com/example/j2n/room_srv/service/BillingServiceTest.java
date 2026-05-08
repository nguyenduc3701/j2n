package com.example.j2n.room_srv.service;

import com.example.j2n.room_srv.controller.request.BillRequest;
import com.example.j2n.room_srv.repository.BillRepository;
import com.example.j2n.room_srv.repository.RoomRepository;
import com.example.j2n.room_srv.repository.entity.BillEntity;
import com.example.j2n.room_srv.repository.entity.RoomEntity;
import com.example.j2n.room_srv.repository.entity.UtilityConfigEntity;
import com.example.j2n.room_srv.messaging.room.publisher.RoomEventPublisher;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UtilityConfigService utilityConfigService;

    @Mock
    private RoomEventPublisher roomEventPublisher;

    @InjectMocks
    private BillingService billingService;

    @Test
    void calculateBill_Success() {
        BillRequest request = BillRequest.builder()
                .roomId(1L)
                .month(5)
                .electricityUsage(100)
                .waterUsage(10)
                .renterId(123L)
                .build();

        RoomEntity room = RoomEntity.builder()
                .id(1L)
                .roomNumber("101")
                .basePrice(BigDecimal.valueOf(2000000))
                .build();

        UtilityConfigEntity electricConfig = UtilityConfigEntity.builder()
                .type("ELECTRIC")
                .unitPrice(BigDecimal.valueOf(3500))
                .build();

        UtilityConfigEntity waterConfig = UtilityConfigEntity.builder()
                .type("WATER")
                .unitPrice(BigDecimal.valueOf(15000))
                .build();

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(utilityConfigService.getActiveConfigs()).thenReturn(List.of(electricConfig, waterConfig));
        when(billRepository.save(any(BillEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        BaseResponse<BillEntity> response = billingService.calculateBill(request);

        assertNotNull(response);
        // 2,000,000 + (100 * 3,500) + (10 * 15,000) = 2,000,000 + 350,000 + 150,000 = 2,500,000
        assertEquals(0, response.getData().getTotalAmount().compareTo(BigDecimal.valueOf(2500000)));
        verify(roomEventPublisher, times(1)).publishRoomBillSynced(any());
    }

    @Test
    void calculateBill_RoomNotFound() {
        BillRequest request = BillRequest.builder().roomId(1L).build();
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> billingService.calculateBill(request));
    }

    @Test
    void getBillsByRoom_Success() {
        BillEntity bill = BillEntity.builder().id("bill-1").build();
        when(billRepository.findByRoomId(1L)).thenReturn(List.of(bill));

        BaseResponse<List<BillEntity>> response = billingService.getBillsByRoom(1L);

        assertEquals(1, response.getData().size());
        verify(billRepository, times(1)).findByRoomId(1L);
    }
}
