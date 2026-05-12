package com.example.j2n.room_srv.service;

import com.example.j2n.room_srv.controller.request.UpdateFeeRequest;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.room_srv.repository.FeeRepository;
import com.example.j2n.room_srv.repository.entity.FeeEntity;
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
class FeeServiceTest {

    @Mock
    private FeeRepository feeRepository;

    @InjectMocks
    private FeeService feeService;

    @Test
    void getActiveFees_Success() {
        FeeEntity fee = FeeEntity.builder().id(1L).isActive(true).build();
        when(feeRepository.findAll()).thenReturn(List.of(fee));

        List<FeeEntity> result = feeService.getActiveFees();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsActive());
        verify(feeRepository, times(1)).findAll();
    }

    @Test
    void updateFee_Success() {
        Long id = 1L;
        UpdateFeeRequest request = UpdateFeeRequest.builder()
                .unitPrice(Optional.of(BigDecimal.valueOf(4000)))
                .name(Optional.of("New Name"))
                .build();
        FeeEntity fee = FeeEntity.builder()
                .id(id)
                .name("Old Name")
                .unitPrice(BigDecimal.valueOf(3500))
                .build();

        when(feeRepository.findById(id)).thenReturn(Optional.of(fee));
        when(feeRepository.save(any(FeeEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        BaseResponse<FeeEntity> response = feeService.updateFee(id, request);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(4000), response.getData().getUnitPrice());
        assertEquals("New Name", response.getData().getName());
    }

    @Test
    void updateFee_NotFound() {
        UpdateFeeRequest request = new UpdateFeeRequest();
        when(feeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> feeService.updateFee(1L, request));
    }
}
