package com.example.j2n.room_srv.controller;

import com.example.j2n.room_srv.controller.request.BillRequest;
import com.example.j2n.room_srv.repository.entity.BillEntity;
import com.example.j2n.room_srv.service.BillingService;
import com.example.j2n.room_srv.service.PaymentService;
import com.example.j2n.utils.ResponseFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BillController.class)
@AutoConfigureMockMvc(addFilters = false)
class BillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BillingService billingService;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void calculateBill_Success() throws Exception {
        BillRequest request = BillRequest.builder()
                .roomId(1L)
                .month(5)
                .renterId(123L)
                .build();
        BillEntity bill = BillEntity.builder().id("bill-1").totalAmount(BigDecimal.valueOf(2000000)).build();
        
        when(billingService.calculateBill(any())).thenReturn(ResponseFactory.success(bill));

        mockMvc.perform(post("/bills/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("bill-1"));
    }


    @Test
    void getBillsByRoom_Success() throws Exception {
        BillEntity bill = BillEntity.builder().id("bill-1").build();
        when(billingService.getBillsByRoom(1L)).thenReturn(ResponseFactory.success(List.of(bill)));

        mockMvc.perform(get("/bills/room/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value("bill-1"));
    }
}
