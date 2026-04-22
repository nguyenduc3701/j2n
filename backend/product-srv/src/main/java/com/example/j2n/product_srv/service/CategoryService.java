package com.example.j2n.product_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.utils.ResponseFactory;
import com.example.j2n.utils.ValidationUtils;
import com.example.j2n.product_srv.constant.MessageEnum;
import com.example.j2n.product_srv.dto.CategoryDto;
import com.example.j2n.product_srv.repository.CategoryRepository;
import com.example.j2n.product_srv.repository.entity.CategoryEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import com.example.j2n.constants.CommonConst;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    @Lazy
    @Autowired
    private CategoryService self;

    private final CategoryRepository categoryRepository;

    @Cacheable(value = CommonConst.CATEGORY_CACHE_KEY, key = CommonConst.ALL_CATEGORIES_KEY)
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
        ValidationUtils.validateString(slug);
        CategoryEntity entity = categoryRepository.findBySlugAndIsDeletedFalse(slug)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.CATEGORY_NOT_FOUND));
        return ResponseFactory.success(entity);
    }

    @LogAround(message = "Get category by name")
    public BaseResponse<CategoryEntity> getCategoryByName(String name) {
        ValidationUtils.validateString(name);
        CategoryEntity entity = categoryRepository.findByNameAndIsDeletedFalse(name)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.CATEGORY_NOT_FOUND));
        return ResponseFactory.success(entity);
    }

    @LogAround(message = "Get categories by type")
    public BaseResponse<List<CategoryEntity>> getCategoriesByType(String type) {
        ValidationUtils.validateString(type);
        return ResponseFactory.success(categoryRepository.findByTypeAndIsDeletedFalse(type));
    }

    @Transactional
    @CacheEvict(value = CommonConst.CATEGORY_CACHE_KEY, allEntries = true)
    @LogAround(message = "Create category")
    public BaseResponse<CategoryEntity> createCategory(CategoryDto input) {
        CategoryEntity entity = CategoryEntity.builder()
                .name(input.getName())
                .slug(input.getSlug())
                .type(input.getType())
                .isDeleted(false)
                .build();
        return ResponseFactory.of(MessageEnum.CREATE_CATEGORY_SUCCESS, categoryRepository.save(entity));
    }

    @Transactional
    @CacheEvict(value = CommonConst.CATEGORY_CACHE_KEY, allEntries = true)
    @LogAround(message = "Update category")
    public BaseResponse<CategoryEntity> updateCategory(Long id, CategoryDto input) {
        CategoryEntity entity = getCategoryByIdOrThrow(id);
        validateCategory(entity);
        entity.setName(input.getName());
        entity.setSlug(input.getSlug());
        if (input.getType() != null) {
            entity.setType(input.getType());
        }
        return ResponseFactory.of(MessageEnum.UPDATE_CATEGORY_SUCCESS, categoryRepository.save(entity));
    }

    @Transactional
    @CacheEvict(value = CommonConst.CATEGORY_CACHE_KEY, allEntries = true)
    @LogAround(message = "Delete category")
    public BaseResponse<Boolean> deleteCategory(Long id) {
        CategoryEntity entity = getCategoryByIdOrThrow(id);
        validateCategory(entity);
        entity.setIsDeleted(true);
        categoryRepository.save(entity);
        return ResponseFactory.of(MessageEnum.DELETE_CATEGORY_SUCCESS, true);
    }

    public CategoryEntity getCategoryByIdOrThrow(Long id) {
        log.info("Get category by id: {}", id);
        ValidationUtils.validateLong(id);
        return categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.CATEGORY_NOT_FOUND));
    }

    private void validateCategory(CategoryEntity entity) {
        log.info("Validate category: {}", entity.getId());
        if (entity.getIsDeleted().equals(Boolean.TRUE)) {
            log.error("Invalid input: category is deleted");
            throw new DataNotFoundException(MessageEnum.CATEGORY_NOT_FOUND);
        }
    }
}
