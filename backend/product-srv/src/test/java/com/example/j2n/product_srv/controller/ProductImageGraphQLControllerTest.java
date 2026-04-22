package com.example.j2n.product_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.product_srv.dto.ProductImageDto;
import com.example.j2n.product_srv.repository.entity.ProductImageEntity;
import com.example.j2n.product_srv.service.ProductImageService;
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
class ProductImageGraphQLControllerTest {

    @Mock
    private ProductImageService productImageService;

    @InjectMocks
    private ProductImageGraphQLController productImageGraphQLController;

    private ProductImageEntity mockImage;
    private ProductImageDto mockDto;
    private BaseResponse<ProductImageEntity> mockResponse;

    @BeforeEach
    void setUp() {
        mockImage = ProductImageEntity.builder()
                .id(1L)
                .imageUrl("http://example.com/image.jpg")
                .isPrimary(false)
                .isDeleted(false)
                .build();

        mockDto = ProductImageDto.builder()
                .productId(1L)
                .imageUrl("http://example.com/image.jpg")
                .isPrimary(false)
                .build();

        mockResponse = new BaseResponse<>();
        mockResponse.setData(mockImage);
    }

    @Test
    void getImagesByProductId_CallsService() {
        BaseResponse<List<ProductImageEntity>> listResponse = new BaseResponse<>();
        listResponse.setData(Collections.singletonList(mockImage));
        when(productImageService.getImagesByProductId(1L)).thenReturn(listResponse);

        BaseResponse<List<ProductImageEntity>> result = productImageGraphQLController.getImagesByProductId(1L);

        assertEquals(listResponse, result);
        verify(productImageService).getImagesByProductId(1L);
    }

    @Test
    void addImageToProduct_CallsService() {
        when(productImageService.addImageToProduct(mockDto)).thenReturn(mockResponse);

        BaseResponse<ProductImageEntity> result = productImageGraphQLController.addImageToProduct(mockDto);

        assertEquals(mockResponse, result);
        verify(productImageService).addImageToProduct(mockDto);
    }

    @Test
    void deleteProductImage_CallsService() {
        BaseResponse<Boolean> boolResponse = new BaseResponse<>();
        boolResponse.setData(true);
        when(productImageService.deleteProductImage(1L)).thenReturn(boolResponse);

        BaseResponse<Boolean> result = productImageGraphQLController.deleteProductImage(1L);

        assertEquals(boolResponse, result);
        verify(productImageService).deleteProductImage(1L);
    }

    @Test
    void setPrimaryImage_CallsService() {
        when(productImageService.setPrimaryImage(1L)).thenReturn(mockResponse);

        BaseResponse<ProductImageEntity> result = productImageGraphQLController.setPrimaryImage(1L);

        assertEquals(mockResponse, result);
        verify(productImageService).setPrimaryImage(1L);
    }
}
