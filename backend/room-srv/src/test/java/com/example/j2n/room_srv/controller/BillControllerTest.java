package com.example.j2n.room_srv.controller;

import com.example.j2n.room_srv.controller.request.BillRequest;
import com.example.j2n.room_srv.controller.request.CalculateAllBillsRequest;
import com.example.j2n.room_srv.controller.request.SearchBillsRequest;
import com.example.j2n.room_srv.controller.request.SearchBillsAdminRequest;
import com.example.j2n.room_srv.service.response.SearchBillsResponse;
import com.example.j2n.room_srv.repository.entity.BillEntity;
import com.example.j2n.room_srv.service.BillingService;
import com.example.j2n.room_srv.service.PaymentService;
import com.example.j2n.utils.ResponseFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BillController.class)
@AutoConfigureMockMvc(addFilters = false)
class BillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BillingService billingService;

    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void searchBills_Success() throws Exception {
        SearchBillsRequest request = new SearchBillsRequest();
        SearchBillsResponse response = new SearchBillsResponse();
        when(billingService.searchBills(any(SearchBillsRequest.class))).thenReturn(ResponseFactory.success(response));

        mockMvc.perform(post("/room/bills/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(billingService, times(1)).searchBills(any(SearchBillsRequest.class));
    }

    @Test
    void searchBillsAdmin_Success() throws Exception {
        SearchBillsAdminRequest request = new SearchBillsAdminRequest();
        SearchBillsResponse response = new SearchBillsResponse();
        when(billingService.searchBillsAdmin(any(SearchBillsAdminRequest.class))).thenReturn(ResponseFactory.success(response));

        mockMvc.perform(post("/room/bills/admin/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(billingService, times(1)).searchBillsAdmin(any(SearchBillsAdminRequest.class));
    }

    @Test
    void calculateBillByRoomId_Success() throws Exception {
        BillRequest request = new BillRequest();
        BillEntity billEntity = new BillEntity();
        when(billingService.calculateBill(any(BillRequest.class))).thenReturn(ResponseFactory.success(billEntity));

        mockMvc.perform(post("/room/bills/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(billingService, times(1)).calculateBill(any(BillRequest.class));
    }

    @Test
    void calculateAllBills_Success() throws Exception {
        CalculateAllBillsRequest request = CalculateAllBillsRequest.builder()
                .month(5)
                .electricIndices(Map.of(1L, 1250))
                .build();
        List<BillEntity> bills = List.of(new BillEntity());
        when(billingService.calculateAllBills(any(CalculateAllBillsRequest.class))).thenReturn(ResponseFactory.success(bills));

        mockMvc.perform(post("/room/bills/calculate-all")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(billingService, times(1)).calculateAllBills(any(CalculateAllBillsRequest.class));
    }

    @Test
    void getBillsByRoomId_Success() throws Exception {
        Long roomId = 1L;
        List<BillEntity> bills = List.of(new BillEntity());
        when(billingService.getBillsByRoomId(roomId)).thenReturn(ResponseFactory.success(bills));

        mockMvc.perform(get("/room/bills/room/" + roomId))
                .andExpect(status().isOk());

        verify(billingService, times(1)).getBillsByRoomId(roomId);
    }

    @Test
    void payBill_Success() throws Exception {
        String billId = "bill-1";
        when(paymentService.initiatePayment(billId)).thenReturn(ResponseFactory.success(new Object()));

        mockMvc.perform(post("/room/bills/" + billId + "/pay"))
                .andExpect(status().isOk());

        verify(paymentService, times(1)).initiatePayment(billId);
    }
}
