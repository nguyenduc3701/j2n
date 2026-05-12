package com.example.j2n.room_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.room_srv.controller.request.BillRequest;
import com.example.j2n.room_srv.controller.request.SearchBillsRequest;
import com.example.j2n.room_srv.controller.response.SearchBillsResponse;
import com.example.j2n.room_srv.repository.entity.BillEntity;
import com.example.j2n.room_srv.service.BillingService;
import com.example.j2n.room_srv.service.PaymentService;
import com.example.j2n.utils.ResponseFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillControllerTest {

    @Mock
    private BillingService billingService;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private BillController billController;

    @Test
    void calculateBillByRoomId_Success() {
        BillRequest request = new BillRequest();
        BillEntity billEntity = new BillEntity();
        when(billingService.calculateBill(request)).thenReturn(ResponseFactory.success(billEntity));

        ResponseEntity<BaseResponse<BillEntity>> result = billController.calculateBillByRoomId(request);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(billEntity, result.getBody().getData());
        verify(billingService, times(1)).calculateBill(request);
    }

    @Test
    void calculateAllBills_Success() {
        Integer month = 5;
        List<BillEntity> bills = List.of(new BillEntity());
        when(billingService.calculateAllBills(month)).thenReturn(ResponseFactory.success(bills));

        ResponseEntity<BaseResponse<List<BillEntity>>> result = billController.calculateAllBills(month);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(bills, result.getBody().getData());
        verify(billingService, times(1)).calculateAllBills(month);
    }

    @Test
    void getBillsByRoom_Success() {
        Long roomId = 1L;
        List<BillEntity> bills = List.of(new BillEntity());
        when(billingService.getBillsByRoomId(roomId)).thenReturn(ResponseFactory.success(bills));

        ResponseEntity<BaseResponse<List<BillEntity>>> result = billController.getBillsByRoomId(roomId);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(bills, result.getBody().getData());
        verify(billingService, times(1)).getBillsByRoomId(roomId);
    }

    @Test
    void payBill_Success() {
        String billId = "bill-1";
        Object paymentResponse = new Object();
        when(paymentService.initiatePayment(billId)).thenReturn(ResponseFactory.success(paymentResponse));

        ResponseEntity<BaseResponse<Object>> result = billController.payBill(billId);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(paymentResponse, result.getBody().getData());
        verify(paymentService, times(1)).initiatePayment(billId);
    }

    @Test
    void searchBills_Success() {
        SearchBillsRequest request = new SearchBillsRequest();
        SearchBillsResponse response = new SearchBillsResponse();
        when(billingService.searchBills(request)).thenReturn(ResponseFactory.success(response));

        ResponseEntity<BaseResponse<SearchBillsResponse>> result = billController.searchBills(request);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(response, result.getBody().getData());
        verify(billingService, times(1)).searchBills(request);
    }
}
