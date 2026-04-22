package com.example.j2n.product_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.product_srv.dto.CategoryDto;
import com.example.j2n.product_srv.repository.entity.CategoryEntity;
import com.example.j2n.product_srv.service.CategoryService;
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
class CategoryGraphQLControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryGraphQLController categoryGraphQLController;

    private CategoryEntity mockEntity;
    private CategoryDto mockDto;
    private BaseResponse<CategoryEntity> mockResponse;

    @BeforeEach
    void setUp() {
        mockEntity = CategoryEntity.builder()
                .id(1L)
                .name("Test Category")
                .slug("test-category")
                .isDeleted(false)
                .build();

        mockDto = CategoryDto.builder()
                .name("Test Category")
                .slug("test-category")
                .build();

        mockResponse = new BaseResponse<>();
        mockResponse.setData(mockEntity);
    }

    @Test
    void getAllCategories_CallsService() {
        BaseResponse<List<CategoryEntity>> listResponse = new BaseResponse<>();
        listResponse.setData(Collections.singletonList(mockEntity));
        when(categoryService.getAllCategories()).thenReturn(listResponse);

        BaseResponse<List<CategoryEntity>> result = categoryGraphQLController.getAllCategories();

        assertEquals(listResponse, result);
        verify(categoryService).getAllCategories();
    }

    @Test
    void getCategoryById_CallsService() {
        when(categoryService.getCategoryById(1L)).thenReturn(mockResponse);

        BaseResponse<CategoryEntity> result = categoryGraphQLController.getCategoryById(1L);

        assertEquals(mockResponse, result);
        verify(categoryService).getCategoryById(1L);
    }

    @Test
    void getCategoryBySlug_CallsService() {
        when(categoryService.getCategoryBySlug("test-category")).thenReturn(mockResponse);

        BaseResponse<CategoryEntity> result = categoryGraphQLController.getCategoryBySlug("test-category");

        assertEquals(mockResponse, result);
        verify(categoryService).getCategoryBySlug("test-category");
    }

    @Test
    void getCategoryByName_CallsService() {
        when(categoryService.getCategoryByName("Test Category")).thenReturn(mockResponse);

        BaseResponse<CategoryEntity> result = categoryGraphQLController.getCategoryByName("Test Category");

        assertEquals(mockResponse, result);
        verify(categoryService).getCategoryByName("Test Category");
    }

    @Test
    void createCategory_CallsService() {
        when(categoryService.createCategory(mockDto)).thenReturn(mockResponse);

        BaseResponse<CategoryEntity> result = categoryGraphQLController.createCategory(mockDto);

        assertEquals(mockResponse, result);
        verify(categoryService).createCategory(mockDto);
    }

    @Test
    void updateCategory_CallsService() {
        when(categoryService.updateCategory(1L, mockDto)).thenReturn(mockResponse);

        BaseResponse<CategoryEntity> result = categoryGraphQLController.updateCategory(1L, mockDto);

        assertEquals(mockResponse, result);
        verify(categoryService).updateCategory(1L, mockDto);
    }

    @Test
    void deleteCategory_CallsService() {
        BaseResponse<Boolean> boolResponse = new BaseResponse<>();
        boolResponse.setData(true);
        when(categoryService.deleteCategory(1L)).thenReturn(boolResponse);

        BaseResponse<Boolean> result = categoryGraphQLController.deleteCategory(1L);

        assertEquals(boolResponse, result);
        verify(categoryService).deleteCategory(1L);
    }
}
