package com.example.j2n.product_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.product_srv.dto.ProductDto;
import com.example.j2n.product_srv.repository.entity.CategoryEntity;
import com.example.j2n.product_srv.repository.entity.ProductEntity;
import com.example.j2n.product_srv.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductGraphQLControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductGraphQLController productGraphQLController;

    private ProductEntity mockProduct;
    private ProductDto mockDto;
    private BaseResponse<ProductEntity> mockResponse;

    @BeforeEach
    void setUp() {
        CategoryEntity mockCategory = CategoryEntity.builder()
                .id(1L)
                .name("Du lịch biển")
                .slug("du-lich-bien")
                .isDeleted(false)
                .build();

        mockProduct = ProductEntity.builder()
                .id(1L)
                .category(mockCategory)
                .title("Product Nha Trang 3N2Đ")
                .description("Khám phá Nha Trang")
                .price(new BigDecimal("2500000.00"))
                .thumbnail("thumbnail.jpg")
                .duration("3 ngày 2 đêm")
                .startLocation("Hà Nội")
                .isDeleted(false)
                .build();

        mockDto = ProductDto.builder()
                .categoryId(1L)
                .title("Product Nha Trang 3N2Đ")
                .description("Khám phá Nha Trang")
                .price(new BigDecimal("2500000.00"))
                .thumbnail("thumbnail.jpg")
                .duration("3 ngày 2 đêm")
                .startLocation("Hà Nội")
                .build();

        mockResponse = new BaseResponse<>();
        mockResponse.setData(mockProduct);
    }

    @Test
    void getAllProducts_CallsService() {
        BaseResponse<List<ProductEntity>> listResponse = new BaseResponse<>();
        listResponse.setData(Collections.singletonList(mockProduct));
        when(productService.getAllProducts()).thenReturn(listResponse);

        BaseResponse<List<ProductEntity>> result = productGraphQLController.getAllProducts();

        assertEquals(listResponse, result);
        verify(productService).getAllProducts();
    }

    @Test
    void getProductById_CallsService() {
        when(productService.getProductById(1L)).thenReturn(mockResponse);

        BaseResponse<ProductEntity> result = productGraphQLController.getProductById(1L);

        assertEquals(mockResponse, result);
        verify(productService).getProductById(1L);
    }

    @Test
    void getProductsByCategory_CallsService() {
        BaseResponse<List<ProductEntity>> listResponse = new BaseResponse<>();
        listResponse.setData(Collections.singletonList(mockProduct));
        when(productService.getProductsByCategory(1L)).thenReturn(listResponse);

        BaseResponse<List<ProductEntity>> result = productGraphQLController.getProductsByCategory(1L);

        assertEquals(listResponse, result);
        verify(productService).getProductsByCategory(1L);
    }

    @Test
    void getProductsByType_CallsService() {
        BaseResponse<List<ProductEntity>> listResponse = new BaseResponse<>();
        listResponse.setData(Collections.singletonList(mockProduct));
        when(productService.getProductsByType("TOUR")).thenReturn(listResponse);

        BaseResponse<List<ProductEntity>> result = productGraphQLController.getProductsByType("TOUR");

        assertEquals(listResponse, result);
        verify(productService).getProductsByType("TOUR");
    }

    @Test
    void createProduct_CallsService() {
        when(productService.createProduct(mockDto)).thenReturn(mockResponse);

        BaseResponse<ProductEntity> result = productGraphQLController.createProduct(mockDto);

        assertEquals(mockResponse, result);
        verify(productService).createProduct(mockDto);
    }

    @Test
    void updateProduct_CallsService() {
        when(productService.updateProduct(1L, mockDto)).thenReturn(mockResponse);

        BaseResponse<ProductEntity> result = productGraphQLController.updateProduct(1L, mockDto);

        assertEquals(mockResponse, result);
        verify(productService).updateProduct(1L, mockDto);
    }

    @Test
    void deleteProduct_CallsService() {
        BaseResponse<Boolean> boolResponse = new BaseResponse<>();
        boolResponse.setData(true);
        when(productService.deleteProduct(1L)).thenReturn(boolResponse);

        BaseResponse<Boolean> result = productGraphQLController.deleteProduct(1L);

        assertEquals(boolResponse, result);
        verify(productService).deleteProduct(1L);
    }
}
