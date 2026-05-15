package com.example.j2n.room_srv.service;

import com.example.j2n.room_srv.controller.request.BillRequest;
import com.example.j2n.room_srv.repository.BillRepository;
import com.example.j2n.room_srv.repository.entity.BillEntity;
import com.example.j2n.room_srv.repository.entity.RoomEntity;
import com.example.j2n.room_srv.repository.entity.RoomMemberEntity;
import com.example.j2n.room_srv.repository.entity.FeeEntity;
import com.example.j2n.room_srv.controller.request.SearchBillsRequest;
import com.example.j2n.room_srv.service.response.BillResponse;
import com.example.j2n.room_srv.service.response.SearchBillsResponse;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.utils.SearchFactory;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private RoomService roomService;

    @Mock
    private FeeService feeService;

    @Mock
    private SearchFactory searchFactory;

    @InjectMocks
    private BillingService billingService;

    @Test
    void calculateBill_Success() {
        BillRequest request = BillRequest.builder()
                .roomId(1L)
                .month(Optional.of(5))
                .electricityNewIndex(100)
                .renterId(123L)
                .build();

        RoomEntity room = RoomEntity.builder()
                .id(1L)
                .roomNumber("101")
                .basePrice(BigDecimal.valueOf(2000000))
                .build();

        FeeEntity electricConfig = FeeEntity.builder()
                .name("ELECTRIC")
                .unitPrice(BigDecimal.valueOf(3500))
                .build();

        FeeEntity waterConfig = FeeEntity.builder()
                .name("WATER")
                .unitPrice(BigDecimal.valueOf(15000))
                .build();

        when(roomService.findRoomByIdOrThrow(1L)).thenReturn(room);
        when(feeService.getActiveFees()).thenReturn(List.of(electricConfig, waterConfig));
        when(billRepository.save(any(BillEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        BaseResponse<BillEntity> response = billingService.calculateBill(request);

        assertNotNull(response);
        // 2,000,000 + (100 * 3,500) = 2,350,000
        assertEquals(0, response.getData().getTotalAmount().compareTo(BigDecimal.valueOf(2350000)));
    }

    @Test
    void calculateBill_RoomNotFound() {
        BillRequest request = BillRequest.builder().roomId(1L).build();
        when(roomService.findRoomByIdOrThrow(1L))
                .thenThrow(new DataNotFoundException(com.example.j2n.room_srv.constant.MessageEnum.ROOM_NOT_FOUND));

        assertThrows(DataNotFoundException.class, () -> billingService.calculateBill(request));
    }

    @Test
    void getBillsByRoom_Success() {
        BillEntity bill = BillEntity.builder().id("bill-1").build();
        when(billRepository.findByRoomId(1L)).thenReturn(List.of(bill));

        BaseResponse<List<BillEntity>> response = billingService.getBillsByRoomId(1L);

        assertEquals(1, response.getData().size());
        verify(billRepository, times(1)).findByRoomId(1L);
    }

    @Test
    void searchBills_Success() {
        SearchBillsRequest request = new SearchBillsRequest();
        request.setRoomId(1L);

        RoomEntity room = RoomEntity.builder().id(1L).roomNumber("101").build();
        when(roomService.findRoomByIdOrThrow(1L)).thenReturn(room);

        Page<BillResponse> page = new PageImpl<>(List.of(
                BillResponse.builder().id("bill-1").roomNumber("101").build()), PageRequest.of(0, 10), 1);

        doReturn(page).when(searchFactory).searchAndMap(any(), anyList(), any(), any());

        BaseResponse<SearchBillsResponse> response = billingService.searchBills(request);

        assertNotNull(response);
        assertEquals(1, response.getData().getBills().size());
        assertEquals("bill-1", response.getData().getBills().get(0).getId());
    }

    @Test
    void validateMonth_Success() {
        int currentMonth = LocalDate.now().getMonthValue();
        when(billRepository.findByRoomIdAndBillingMonth(1L, currentMonth)).thenReturn(List.of());

        Integer result = billingService.validateMonth(null, 1L);
        assertEquals(currentMonth, result);
    }

    @Test
    void validateMonth_FutureMonth_ThrowsException() {
        int futureMonth = LocalDate.now().getMonthValue() + 1;
        if (futureMonth > 12) {
            return; // Skip if current month is December
        }
        assertThrows(InvalidInputException.class, () -> billingService.validateMonth(futureMonth, 1L));
    }

    @Test
    void validateMonth_BillAlreadyExists_ThrowsException() {
        int currentMonth = LocalDate.now().getMonthValue();
        when(billRepository.findByRoomIdAndBillingMonth(1L, currentMonth)).thenReturn(List.of(new BillEntity()));

        assertThrows(InvalidInputException.class, () -> billingService.validateMonth(currentMonth, 1L));
    }

    @Test
    void calculateAllBills_Success() {
        RoomMemberEntity member = new RoomMemberEntity();
        member.setUserId(123L);
        member.setIsPrimary(true);

        RoomEntity room = RoomEntity.builder()
                .id(1L)
                .roomNumber("101")
                .basePrice(BigDecimal.valueOf(2000000))
                .members(List.of(member))
                .build();

        when(roomService.getAllRooms()).thenReturn(List.of(room));
        when(feeService.getActiveFees()).thenReturn(List.of());
        when(billRepository.saveAll(anyList())).thenAnswer(i -> i.getArguments()[0]);

        BaseResponse<List<BillEntity>> response = billingService.calculateAllBills(5);

        assertNotNull(response);
        assertEquals(1, response.getData().size());
        verify(billRepository, times(1)).saveAll(anyList());
    }

    @Test
    void calculateAllBills_NoPrimaryRenter_ReturnsEmpty() {
        RoomEntity room = RoomEntity.builder()
                .id(1L)
                .roomNumber("101")
                .members(List.of())
                .build();

        when(roomService.getAllRooms()).thenReturn(List.of(room));

        BaseResponse<List<BillEntity>> response = billingService.calculateAllBills(5);

        assertNotNull(response);
        assertTrue(response.getData().isEmpty());
        verify(billRepository, never()).saveAll(anyList());
    }
}
