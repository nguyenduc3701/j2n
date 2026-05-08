package com.example.j2n.room_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.room_srv.repository.UtilityConfigRepository;
import com.example.j2n.room_srv.repository.entity.UtilityConfigEntity;
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
class UtilityConfigServiceTest {

    @Mock
    private UtilityConfigRepository utilityConfigRepository;

    @InjectMocks
    private UtilityConfigService utilityConfigService;

    @Test
    void getActiveConfigs_Success() {
        UtilityConfigEntity config = UtilityConfigEntity.builder().id(1L).isActive(true).build();
        when(utilityConfigRepository.findByIsActiveTrue()).thenReturn(List.of(config));

        List<UtilityConfigEntity> result = utilityConfigService.getActiveConfigs();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsActive());
        verify(utilityConfigRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    void updatePrice_Success() {
        Long id = 1L;
        BigDecimal newPrice = BigDecimal.valueOf(4000);
        UtilityConfigEntity config = UtilityConfigEntity.builder().id(id).unitPrice(BigDecimal.valueOf(3500)).build();

        when(utilityConfigRepository.findById(id)).thenReturn(Optional.of(config));
        when(utilityConfigRepository.save(any(UtilityConfigEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        BaseResponse<UtilityConfigEntity> response = utilityConfigService.updatePrice(id, newPrice);

        assertNotNull(response);
        assertEquals(newPrice, response.getData().getUnitPrice());
    }

    @Test
    void updatePrice_NotFound() {
        when(utilityConfigRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> utilityConfigService.updatePrice(1L, BigDecimal.TEN));
    }
}
