package com.example.j2n.travel_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.utils.ResponseFactory;
import com.example.j2n.travel_srv.constant.MessageEnum;
import com.example.j2n.travel_srv.dto.CategoryDto;
import com.example.j2n.travel_srv.repository.CategoryRepository;
import com.example.j2n.travel_srv.repository.entity.CategoryEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @LogAround(message = "Get all categories")
    public BaseResponse<List<CategoryEntity>> getAllCategories() {
        return ResponseFactory.success(categoryRepository.findAllByIsDeletedFalse());
    }

    @LogAround(message = "Get category by id")
    public BaseResponse<CategoryEntity> getCategoryById(Long id) {
        return ResponseFactory.success(getCategoryByIdOrThrow(id));
    }

    @LogAround(message = "Get category by slug")
    public BaseResponse<CategoryEntity> getCategoryBySlug(String slug) {
        validateString(slug);
        CategoryEntity entity = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.CATEGORY_NOT_FOUND));
        return ResponseFactory.success(entity);
    }

    @LogAround(message = "Get category by name")
    public BaseResponse<CategoryEntity> getCategoryByName(String name) {
        validateString(name);
        CategoryEntity entity = categoryRepository.findByName(name)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.CATEGORY_NOT_FOUND));
        return ResponseFactory.success(entity);
    }

    @Transactional
    @LogAround(message = "Create category")
    public BaseResponse<CategoryEntity> createCategory(CategoryDto input) {
        CategoryEntity entity = CategoryEntity.builder()
                .name(input.getName())
                .slug(input.getSlug())
                .isDeleted(false)
                .build();
        return ResponseFactory.success(categoryRepository.save(entity));
    }

    @Transactional
    @LogAround(message = "Update category")
    public BaseResponse<CategoryEntity> updateCategory(Long id, CategoryDto input) {
        CategoryEntity entity = getCategoryByIdOrThrow(id);
        validateCategory(entity);
        entity.setName(input.getName());
        entity.setSlug(input.getSlug());
        return ResponseFactory.success(categoryRepository.save(entity));
    }

    @Transactional
    @LogAround(message = "Delete category")
    public BaseResponse<Boolean> deleteCategory(Long id) {
        CategoryEntity entity = getCategoryByIdOrThrow(id);
        validateCategory(entity);
        entity.setIsDeleted(true);
        categoryRepository.save(entity);
        return ResponseFactory.success(true);
    }

    private CategoryEntity getCategoryByIdOrThrow(Long id) {
        validateLong(id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.CATEGORY_NOT_FOUND));
    }

    private void validateCategory(CategoryEntity entity) {
        if (entity.getIsDeleted().equals(Boolean.TRUE)) {
            throw new DataNotFoundException(MessageEnum.CATEGORY_NOT_FOUND);
        }
    }

    private void validateString(String str) {
        if (str == null || str.isBlank()) {
            log.error("Invalid input: string is null or blank");
            throw new InvalidInputException(BaseMessageEnum.BAD_REQUEST);
        }
    }

    private void validateLong(Long value) {
        if (value == null || value <= 0) {
            log.error("Invalid input: value is null or less than or equal to 0");
            throw new InvalidInputException(BaseMessageEnum.BAD_REQUEST);
        }
    }
}
