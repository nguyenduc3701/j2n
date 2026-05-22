package com.example.j2n.room_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.room_srv.controller.request.CreateFeeRequest;
import com.example.j2n.room_srv.controller.request.UpdateFeeRequest;
import com.example.j2n.room_srv.service.FeeService;
import com.example.j2n.room_srv.service.response.FeeResponse;
import com.example.j2n.utils.ResponseFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeeController.class)
@AutoConfigureMockMvc(addFilters = false)
class FeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FeeService feeService;

    @MockitoBean
    private org.springframework.data.redis.connection.RedisConnectionFactory redisConnectionFactory;

    @MockitoBean
    private org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getActiveFees_Success() throws Exception {
        List<FeeResponse> fees = List.of(new FeeResponse());
        when(feeService.getActiveFees()).thenReturn(fees);

        mockMvc.perform(get("/fees"))
                .andExpect(status().isOk());

        verify(feeService, times(1)).getActiveFees();
    }

    @Test
    void createFee_Success() throws Exception {
        CreateFeeRequest request = CreateFeeRequest.builder()
                .name("Internet")
                .unitPrice(BigDecimal.valueOf(200000))
                .unitName("month")
                .build();
        FeeResponse feeResponse = new FeeResponse();
        when(feeService.createFee(any(CreateFeeRequest.class))).thenReturn(ResponseFactory.success(feeResponse));

        mockMvc.perform(post("/fees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(feeService, times(1)).createFee(any(CreateFeeRequest.class));
    }

    @Test
    void updateFee_Success() throws Exception {
        Long feeId = 1L;
        UpdateFeeRequest request = UpdateFeeRequest.builder()
                .name(java.util.Optional.of("Internet"))
                .build();
        FeeResponse feeResponse = new FeeResponse();
        when(feeService.updateFee(eq(feeId), any(UpdateFeeRequest.class))).thenReturn(ResponseFactory.success(feeResponse));

        // Use PUT here to match the updated controller
        mockMvc.perform(put("/fees/" + feeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(feeService, times(1)).updateFee(eq(feeId), any(UpdateFeeRequest.class));
    }
}


