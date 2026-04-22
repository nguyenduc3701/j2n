package com.example.j2n.product_srv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.product_srv.dto.ProductScheduleDto;
import com.example.j2n.product_srv.repository.ProductRepository;
import com.example.j2n.product_srv.repository.ProductScheduleRepository;
import com.example.j2n.product_srv.repository.entity.ProductEntity;
import com.example.j2n.product_srv.repository.entity.ProductScheduleEntity;

@ExtendWith(MockitoExtension.class)
class ProductScheduleServiceTest {

    @Mock
    private ProductScheduleRepository productScheduleRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductScheduleService productScheduleService;

    private ProductEntity mockProduct;
    private ProductScheduleEntity mockSchedule;
    private ProductScheduleDto mockDto;

    @BeforeEach
    void setUp() {
        mockProduct = ProductEntity.builder()
                .id(1L)
                .title("Product Test")
                .isDeleted(false)
                .build();

        mockSchedule = ProductScheduleEntity.builder()
                .id(1L)
                .product(mockProduct)
                .dayNumber(1)
                .title("Ngày 1: Khởi hành")
                .content("Bắt đầu chuyến đi")
                .isDeleted(false)
                .build();

        mockDto = ProductScheduleDto.builder()
                .productId(1L)
                .dayNumber(1)
                .title("Ngày 1: Khởi hành")
                .content("Bắt đầu chuyến đi")
                .build();
    }

    @Test
    void getSchedulesByProductId_Success() {
        when(productScheduleRepository.findAllByProductIdAndIsDeletedFalseOrderByDayNumberAsc(1L))
                .thenReturn(Collections.singletonList(mockSchedule));

        BaseResponse<List<ProductScheduleEntity>> response = productScheduleService.getSchedulesByProductId(1L);

        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        verify(productScheduleRepository, times(1)).findAllByProductIdAndIsDeletedFalseOrderByDayNumberAsc(1L);
    }

    @Test
    void addScheduleToProduct_Success() {
        when(productRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockProduct));
        when(productScheduleRepository.save(any(ProductScheduleEntity.class))).thenReturn(mockSchedule);

        BaseResponse<ProductScheduleEntity> response = productScheduleService.addScheduleToProduct(mockDto);

        assertNotNull(response.getData());
        assertEquals(mockSchedule.getTitle(), response.getData().getTitle());
        verify(productRepository, times(1)).findByIdAndIsDeletedFalse(1L);
        verify(productScheduleRepository, times(1)).save(any(ProductScheduleEntity.class));
    }

    @Test
    void updateProductSchedule_Success() {
        when(productScheduleRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockSchedule));
        when(productScheduleRepository.save(any(ProductScheduleEntity.class))).thenReturn(mockSchedule);

        BaseResponse<ProductScheduleEntity> response = productScheduleService.updateProductSchedule(1L, mockDto);

        assertNotNull(response.getData());
        verify(productScheduleRepository, times(1)).findByIdAndIsDeletedFalse(1L);
        verify(productScheduleRepository, times(1)).save(any(ProductScheduleEntity.class));
    }

    @Test
    void deleteProductSchedule_Success() {
        when(productScheduleRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockSchedule));
        when(productScheduleRepository.save(any(ProductScheduleEntity.class))).thenReturn(mockSchedule);

        BaseResponse<Boolean> response = productScheduleService.deleteProductSchedule(1L);

        assertTrue(response.getData());
        assertTrue(mockSchedule.getIsDeleted());
        verify(productScheduleRepository, times(1)).save(mockSchedule);
    }

    @Test
    void validateLong_Fail() {
        assertThrows(InvalidInputException.class, () -> productScheduleService.getSchedulesByProductId(null));
        assertThrows(InvalidInputException.class, () -> productScheduleService.getSchedulesByProductId(0L));
    }
}
