package com.example.j2n.room_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.room_srv.controller.request.UpdateFeeRequest;
import com.example.j2n.room_srv.repository.entity.FeeEntity;
import com.example.j2n.room_srv.service.FeeService;
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
class FeeControllerTest {

    @Mock
    private FeeService feeService;

    @InjectMocks
    private FeeController feeController;

    @Test
    void getActiveFees_Success() {
        List<FeeEntity> fees = List.of(new FeeEntity());
        when(feeService.getActiveFees()).thenReturn(fees);

        ResponseEntity<BaseResponse<List<FeeEntity>>> result = feeController.getActiveFees();

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(fees, result.getBody().getData());
        verify(feeService, times(1)).getActiveFees();
    }

    @Test
    void updateFee_Success() {
        Long feeId = 1L;
        UpdateFeeRequest request = new UpdateFeeRequest();
        FeeEntity feeEntity = new FeeEntity();
        when(feeService.updateFee(feeId, request)).thenReturn(ResponseFactory.success(feeEntity));

        ResponseEntity<BaseResponse<FeeEntity>> result = feeController.updateFee(feeId, request);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(feeEntity, result.getBody().getData());
        verify(feeService, times(1)).updateFee(feeId, request);
    }
}
