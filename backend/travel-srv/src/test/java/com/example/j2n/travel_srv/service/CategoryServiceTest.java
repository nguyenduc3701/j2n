package com.example.j2n.travel_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.travel_srv.dto.CategoryDto;
import com.example.j2n.travel_srv.repository.CategoryRepository;
import com.example.j2n.travel_srv.repository.entity.CategoryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private CategoryEntity mockEntity;
    private CategoryDto mockDto;

    @BeforeEach
    void setUp() {
        mockEntity = CategoryEntity.builder()
                .id(1L)
                .name("Test Category")
                .slug("test-category")
                .isDeleted(false)
                .build();

        mockDto = CategoryDto.builder()
                .name("New Category")
                .slug("new-category")
                .build();
    }

    @Test
    void getAllCategories_Success() {
        when(categoryRepository.findAllByIsDeletedFalse()).thenReturn(Collections.singletonList(mockEntity));

        BaseResponse<List<CategoryEntity>> response = categoryService.getAllCategories();

        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        verify(categoryRepository, times(1)).findAllByIsDeletedFalse();
    }

    @Test
    void getCategoryById_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockEntity));

        BaseResponse<CategoryEntity> response = categoryService.getCategoryById(1L);

        assertNotNull(response.getData());
        assertEquals(mockEntity.getName(), response.getData().getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getCategoryById_Fail_NotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    void getCategoryById_Fail_InvalidId() {
        assertThrows(InvalidInputException.class, () -> categoryService.getCategoryById(null));
        assertThrows(InvalidInputException.class, () -> categoryService.getCategoryById(0L));
    }

    @Test
    void getCategoryBySlug_Success() {
        when(categoryRepository.findBySlug("test-category")).thenReturn(Optional.of(mockEntity));

        BaseResponse<CategoryEntity> response = categoryService.getCategoryBySlug("test-category");

        assertNotNull(response.getData());
        assertEquals(mockEntity.getSlug(), response.getData().getSlug());
    }

    @Test
    void getCategoryBySlug_Fail_NotFound() {
        when(categoryRepository.findBySlug("test-category")).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> categoryService.getCategoryBySlug("test-category"));
    }

    @Test
    void getCategoryBySlug_Fail_InvalidInput() {
        assertThrows(InvalidInputException.class, () -> categoryService.getCategoryBySlug(null));
        assertThrows(InvalidInputException.class, () -> categoryService.getCategoryBySlug(""));
        assertThrows(InvalidInputException.class, () -> categoryService.getCategoryBySlug("   "));
    }

    @Test
    void getCategoryByName_Success() {
        when(categoryRepository.findByName("Test Category")).thenReturn(Optional.of(mockEntity));

        BaseResponse<CategoryEntity> response = categoryService.getCategoryByName("Test Category");

        assertNotNull(response.getData());
        assertEquals(mockEntity.getName(), response.getData().getName());
    }

    @Test
    void getCategoryByName_Fail_NotFound() {
        when(categoryRepository.findByName("Test Category")).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> categoryService.getCategoryByName("Test Category"));
    }

    @Test
    void getCategoryByName_Fail_InvalidInput() {
        assertThrows(InvalidInputException.class, () -> categoryService.getCategoryByName(null));
        assertThrows(InvalidInputException.class, () -> categoryService.getCategoryByName(""));
        assertThrows(InvalidInputException.class, () -> categoryService.getCategoryByName("   "));
    }

    @Test
    void createCategory_Success() {
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(mockEntity);

        BaseResponse<CategoryEntity> response = categoryService.createCategory(mockDto);

        assertNotNull(response.getData());
        verify(categoryRepository, times(1)).save(any(CategoryEntity.class));
    }

    @Test
    void updateCategory_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockEntity));
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(mockEntity);

        BaseResponse<CategoryEntity> response = categoryService.updateCategory(1L, mockDto);

        assertNotNull(response.getData());
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(CategoryEntity.class));
    }

    @Test
    void updateCategory_Fail_AlreadyDeleted() {
        mockEntity.setIsDeleted(true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockEntity));

        assertThrows(DataNotFoundException.class, () -> categoryService.updateCategory(1L, mockDto));
    }

    @Test
    void deleteCategory_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockEntity));
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(mockEntity);

        BaseResponse<Boolean> response = categoryService.deleteCategory(1L);

        assertNotNull(response.getData());
        assertTrue(response.getData());
        assertTrue(mockEntity.getIsDeleted());
        verify(categoryRepository, times(1)).save(any(CategoryEntity.class));
    }

    @Test
    void deleteCategory_Fail_AlreadyDeleted() {
        mockEntity.setIsDeleted(true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockEntity));

        assertThrows(DataNotFoundException.class, () -> categoryService.deleteCategory(1L));
    }
}
