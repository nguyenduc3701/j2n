package com.example.j2n.travel_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.travel_srv.dto.CategoryDto;
import com.example.j2n.travel_srv.entity.CategoryEntity;
import com.example.j2n.travel_srv.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CategoryGraphQLController {

    private final CategoryService categoryService;

    // --- Queries ---

    @QueryMapping(name = "getAllCategories")
    public BaseResponse<List<CategoryEntity>> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @QueryMapping(name = "getCategoryById")
    public BaseResponse<CategoryEntity> getCategoryById(@Argument Long id) {
        return categoryService.getCategoryById(id);
    }

    @QueryMapping(name = "getCategoryBySlug")
    public BaseResponse<CategoryEntity> getCategoryBySlug(@Argument String slug) {
        return categoryService.getCategoryBySlug(slug);
    }

    @QueryMapping(name = "getCategoryByName")
    public BaseResponse<CategoryEntity> getCategoryByName(@Argument String name) {
        return categoryService.getCategoryByName(name);
    }

    // --- Mutations ---

    @MutationMapping(name = "createCategory")
    public BaseResponse<CategoryEntity> createCategory(@Argument @Valid CategoryDto input) {
        return categoryService.createCategory(input);
    }

    @MutationMapping(name = "updateCategory")
    public BaseResponse<CategoryEntity> updateCategory(@Argument Long id, @Argument @Valid CategoryDto input) {
        return categoryService.updateCategory(id, input);
    }

    @MutationMapping(name = "deleteCategory")
    public BaseResponse<Boolean> deleteCategory(@Argument Long id) {
        return categoryService.deleteCategory(id);
    }
}
