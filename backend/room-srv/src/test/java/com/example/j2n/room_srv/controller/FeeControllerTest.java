package com.example.j2n.room_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.room_srv.controller.request.UpdateFeeRequest;
import com.example.j2n.room_srv.repository.entity.FeeEntity;
import com.example.j2n.room_srv.service.FeeService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeeController.class)
@AutoConfigureMockMvc(addFilters = false)
class FeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FeeService feeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getActiveFees_Success() throws Exception {
        List<FeeEntity> fees = List.of(new FeeEntity());
        when(feeService.getActiveFees()).thenReturn(fees);

        mockMvc.perform(get("/room/fees"))
                .andExpect(status().isOk());

        verify(feeService, times(1)).getActiveFees();
    }

    @Test
    void updateFee_Success() throws Exception {
        Long feeId = 1L;
        UpdateFeeRequest request = new UpdateFeeRequest();
        FeeEntity feeEntity = new FeeEntity();
        when(feeService.updateFee(eq(feeId), any(UpdateFeeRequest.class))).thenReturn(ResponseFactory.success(feeEntity));

        // Use PUT here to match the updated controller
        mockMvc.perform(put("/room/fees/" + feeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(feeService, times(1)).updateFee(eq(feeId), any(UpdateFeeRequest.class));
    }
}
