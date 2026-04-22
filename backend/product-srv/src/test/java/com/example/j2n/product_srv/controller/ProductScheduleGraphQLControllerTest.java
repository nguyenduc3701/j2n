package com.example.j2n.product_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.product_srv.dto.ProductScheduleDto;
import com.example.j2n.product_srv.repository.entity.ProductScheduleEntity;
import com.example.j2n.product_srv.service.ProductScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductScheduleGraphQLControllerTest {

    @Mock
    private ProductScheduleService productScheduleService;

    @InjectMocks
    private ProductScheduleGraphQLController productScheduleGraphQLController;

    private ProductScheduleEntity mockSchedule;
    private ProductScheduleDto mockDto;
    private BaseResponse<ProductScheduleEntity> mockResponse;

    @BeforeEach
    void setUp() {
        mockSchedule = ProductScheduleEntity.builder()
                .id(1L)
                .dayNumber(1)
                .title("Ngày 1")
                .content("Nội dung")
                .isDeleted(false)
                .build();

        mockDto = ProductScheduleDto.builder()
                .productId(1L)
                .dayNumber(1)
                .title("Ngày 1")
                .content("Nội dung")
                .build();

        mockResponse = new BaseResponse<>();
        mockResponse.setData(mockSchedule);
    }

    @Test
    void getSchedulesByProductId_CallsService() {
        BaseResponse<List<ProductScheduleEntity>> listResponse = new BaseResponse<>();
        listResponse.setData(Collections.singletonList(mockSchedule));
        when(productScheduleService.getSchedulesByProductId(1L)).thenReturn(listResponse);

        BaseResponse<List<ProductScheduleEntity>> result = productScheduleGraphQLController.getSchedulesByProductId(1L);

        assertEquals(listResponse, result);
        verify(productScheduleService).getSchedulesByProductId(1L);
    }

    @Test
    void addScheduleToProduct_CallsService() {
        when(productScheduleService.addScheduleToProduct(mockDto)).thenReturn(mockResponse);

        BaseResponse<ProductScheduleEntity> result = productScheduleGraphQLController.addScheduleToProduct(mockDto);

        assertEquals(mockResponse, result);
        verify(productScheduleService).addScheduleToProduct(mockDto);
    }

    @Test
    void updateProductSchedule_CallsService() {
        when(productScheduleService.updateProductSchedule(1L, mockDto)).thenReturn(mockResponse);

        BaseResponse<ProductScheduleEntity> result = productScheduleGraphQLController.updateProductSchedule(1L, mockDto);

        assertEquals(mockResponse, result);
        verify(productScheduleService).updateProductSchedule(1L, mockDto);
    }

    @Test
    void deleteProductSchedule_CallsService() {
        BaseResponse<Boolean> boolResponse = new BaseResponse<>();
        boolResponse.setData(true);
        when(productScheduleService.deleteProductSchedule(1L)).thenReturn(boolResponse);

        BaseResponse<Boolean> result = productScheduleGraphQLController.deleteProductSchedule(1L);

        assertEquals(boolResponse, result);
        verify(productScheduleService).deleteProductSchedule(1L);
    }
}
