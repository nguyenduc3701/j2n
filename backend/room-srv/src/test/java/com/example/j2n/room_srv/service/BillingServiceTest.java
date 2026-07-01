package com.example.j2n.room_srv.service;

import com.example.j2n.room_srv.controller.request.BillRequest;
import com.example.j2n.room_srv.controller.request.CalculateAllBillsRequest;
import com.example.j2n.room_srv.repository.BillRepository;
import com.example.j2n.room_srv.repository.entity.BillEntity;
import com.example.j2n.room_srv.repository.entity.RoomEntity;
import com.example.j2n.room_srv.repository.entity.RoomMemberEntity;
import com.example.j2n.room_srv.service.response.FeeResponse;
import com.example.j2n.room_srv.controller.request.SearchBillsRequest;
import com.example.j2n.room_srv.controller.request.SearchBillsAdminRequest;
import com.example.j2n.room_srv.utils.RoomSecurityUtil;
import com.example.j2n.room_srv.messaging.room.publisher.RoomEventPublisher;
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
import java.util.Map;

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

    @Mock
    private RoomSecurityUtil roomSecurityUtil;

    @Mock
    private RoomEventPublisher eventPublisher;

    @InjectMocks
    private BillingService billingService;

    @Test
    void calculateBill_Success() {
        BillRequest request = BillRequest.builder()
                .roomId(1L)
                .month(Optional.of(5))
                .electricityNewIndex(100)
                .build();

        RoomMemberEntity member = new RoomMemberEntity();
        member.setUserId(123L);

        com.example.j2n.room_srv.repository.entity.FeeEntity electricConfig = com.example.j2n.room_srv.repository.entity.FeeEntity.builder()
                .name("Electricity")
                .unitName("Unit")
                .unitPrice(BigDecimal.valueOf(3500))
                .isActive(true)
                .build();

        com.example.j2n.room_srv.repository.entity.RoomFeeEntity roomElectric = com.example.j2n.room_srv.repository.entity.RoomFeeEntity.builder()
                .fee(electricConfig)
                .build();

        com.example.j2n.room_srv.repository.entity.FeeEntity waterConfig = com.example.j2n.room_srv.repository.entity.FeeEntity.builder()
                .name("Water")
                .unitName("Person")
                .unitPrice(BigDecimal.valueOf(15000))
                .isActive(true)
                .build();

        com.example.j2n.room_srv.repository.entity.RoomFeeEntity roomWater = com.example.j2n.room_srv.repository.entity.RoomFeeEntity.builder()
                .fee(waterConfig)
                .build();

        RoomEntity room = RoomEntity.builder()
                .id(1L)
                .roomNumber("101")
                .basePrice(BigDecimal.valueOf(2000000))
                .currentElectricIndex(0)
                .members(List.of(member))
                .fees(List.of(roomElectric, roomWater))
                .build();

        when(roomService.findRoomByIdOrThrow(1L)).thenReturn(room);
        when(billRepository.save(any(BillEntity.class))).thenAnswer(i -> {
            BillEntity entity = (BillEntity) i.getArguments()[0];
            entity.setRoom(room);
            return entity;
        });

        BaseResponse<BillResponse> response = billingService.calculateBill(request);

        assertNotNull(response);
        // Base Price: 2,000,000
        // Electric Usage: 100 * 3,500 = 350,000
        // Member Count: 1 * 15,000 = 15,000
        // Total: 2,365,000
        assertEquals(0, response.getData().getTotalAmount().compareTo(BigDecimal.valueOf(2365000)));
    }

    @Test
    void calculateBill_RoomNotFound() {
        BillRequest request = BillRequest.builder()
                .roomId(1L)
                .electricityNewIndex(100)
                .build();
        when(roomService.findRoomByIdOrThrow(1L))
                .thenThrow(new DataNotFoundException(com.example.j2n.room_srv.constant.MessageEnum.ROOM_NOT_FOUND));

        assertThrows(DataNotFoundException.class, () -> billingService.calculateBill(request));
    }

    @Test
    void getBillsByRoom_Success() {
        RoomEntity room = RoomEntity.builder().id(1L).roomNumber("101").build();
        BillEntity bill = BillEntity.builder().id("bill-1").build();

        when(roomService.findRoomByIdOrThrow(1L)).thenReturn(room);
        doNothing().when(roomSecurityUtil).checkRoomAccess(room);
        when(billRepository.findByRoomId(1L)).thenReturn(List.of(bill));

        BaseResponse<List<BillResponse>> response = billingService.getBillsByRoomId(1L);

        assertEquals(1, response.getData().size());
        verify(roomService, times(1)).findRoomByIdOrThrow(1L);
        verify(roomSecurityUtil, times(1)).checkRoomAccess(room);
        verify(billRepository, times(1)).findByRoomId(1L);
    }

    @Test
    void getBillsByRoom_AccessDenied_ThrowsException() {
        RoomEntity room = RoomEntity.builder().id(1L).roomNumber("101").build();

        when(roomService.findRoomByIdOrThrow(1L)).thenReturn(room);
        doThrow(new com.example.j2n.exception.AccessDeniedException(com.example.j2n.enums.BaseMessageEnum.ACCESS_DENIED))
                .when(roomSecurityUtil).checkRoomAccess(room);

        assertThrows(com.example.j2n.exception.AccessDeniedException.class, () -> billingService.getBillsByRoomId(1L));
        verify(roomService, times(1)).findRoomByIdOrThrow(1L);
        verify(roomSecurityUtil, times(1)).checkRoomAccess(room);
        verify(billRepository, never()).findByRoomId(any());
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
    void searchBillsAdmin_WithRoomId_Success() {
        SearchBillsAdminRequest request = new SearchBillsAdminRequest();
        request.setRoomId(Optional.of(1L));

        RoomEntity room = RoomEntity.builder().id(1L).roomNumber("101").build();
        when(roomService.findRoomByIdOrThrow(1L)).thenReturn(room);

        Page<BillResponse> page = new PageImpl<>(List.of(
                BillResponse.builder().id("bill-1").roomNumber("101").build()), PageRequest.of(0, 10), 1);

        doReturn(page).when(searchFactory).searchAndMap(any(), anyList(), any(), any());

        BaseResponse<SearchBillsResponse> response = billingService.searchBillsAdmin(request);

        assertNotNull(response);
        assertEquals(1, response.getData().getBills().size());
        assertEquals("bill-1", response.getData().getBills().get(0).getId());
    }

    @Test
    void searchBillsAdmin_WithoutRoomId_Success() {
        SearchBillsAdminRequest request = new SearchBillsAdminRequest();
        request.setRoomId(Optional.empty());

        Page<BillResponse> page = new PageImpl<>(List.of(
                BillResponse.builder().id("bill-1").roomNumber("101").build()), PageRequest.of(0, 10), 1);

        doReturn(page).when(searchFactory).searchAndMap(any(), anyList(), any(), any());

        BaseResponse<SearchBillsResponse> response = billingService.searchBillsAdmin(request);

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

        CalculateAllBillsRequest request = CalculateAllBillsRequest.builder()
                .month(5)
                .electricIndices(Map.of(1L, 100))
                .build();

        when(roomService.getAllRooms()).thenReturn(List.of(room));
        when(billRepository.saveAll(anyList())).thenAnswer(i -> i.getArguments()[0]);

        BaseResponse<List<BillResponse>> response = billingService.calculateAllBills(request);

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

        CalculateAllBillsRequest request = CalculateAllBillsRequest.builder()
                .month(5)
                .electricIndices(Map.of())
                .build();

        when(roomService.getAllRooms()).thenReturn(List.of(room));

        BaseResponse<List<BillResponse>> response = billingService.calculateAllBills(request);

        assertNotNull(response);
        assertTrue(response.getData().isEmpty());
        verify(billRepository, never()).saveAll(anyList());
    }
}
