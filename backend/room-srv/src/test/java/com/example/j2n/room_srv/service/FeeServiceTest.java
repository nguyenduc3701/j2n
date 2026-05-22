package com.example.j2n.room_srv.service;

import com.example.j2n.room_srv.controller.request.CreateFeeRequest;
import com.example.j2n.room_srv.controller.request.UpdateFeeRequest;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.room_srv.repository.FeeRepository;
import com.example.j2n.room_srv.repository.entity.FeeEntity;
import com.example.j2n.room_srv.service.response.FeeResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
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

        List<FeeResponse> result = feeService.getActiveFees();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsActive());
        verify(feeRepository, times(1)).findAll();
    }

    @Test
    void findByIdOrThrow_Success() {
        Long id = 1L;
        FeeEntity fee = FeeEntity.builder().id(id).name("Electricity").build();
        when(feeRepository.findById(id)).thenReturn(Optional.of(fee));

        FeeEntity result = feeService.findByIdOrThrow(id);

        assertNotNull(result);
        assertEquals("Electricity", result.getName());
    }

    @Test
    void findByIdOrThrow_NotFound() {
        Long id = 1L;
        when(feeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> feeService.findByIdOrThrow(id));
    }

    @Test
    void findAllByIds_Success() {
        List<Long> ids = List.of(1L, 2L);
        FeeEntity fee1 = FeeEntity.builder().id(1L).build();
        FeeEntity fee2 = FeeEntity.builder().id(2L).build();
        when(feeRepository.findAllById(ids)).thenReturn(List.of(fee1, fee2));

        List<FeeEntity> result = feeService.findAllByIds(ids);

        assertEquals(2, result.size());
        verify(feeRepository, times(1)).findAllById(ids);
    }

    @Test
    void findAllByIds_NullList_ThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> feeService.findAllByIds(null));
    }

    @Test
    void findAllByIds_EmptyList_ThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> feeService.findAllByIds(Collections.emptyList()));
    }

    @Test
    void createFee_Success() {
        CreateFeeRequest request = CreateFeeRequest.builder()
                .name("Internet")
                .unitPrice(BigDecimal.valueOf(200000))
                .unitName("month")
                .build();

        when(feeRepository.save(any(FeeEntity.class))).thenAnswer(i -> {
            FeeEntity saved = (FeeEntity) i.getArguments()[0];
            saved.setId(10L);
            return saved;
        });

        BaseResponse<FeeResponse> response = feeService.createFee(request);

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(10L, response.getData().getId());
        assertEquals("Internet", response.getData().getName());
        assertEquals(BigDecimal.valueOf(200000), response.getData().getUnitPrice());
        assertEquals("month", response.getData().getUnitName());
        assertTrue(response.getData().getIsActive());
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

        BaseResponse<FeeResponse> response = feeService.updateFee(id, request);

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

