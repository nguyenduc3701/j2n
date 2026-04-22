package com.example.j2n.bff_srv.controller;

import com.example.j2n.bff_srv.controller.request.CategoryRequest;
import com.example.j2n.bff_srv.controller.request.ProductImageRequest;
import com.example.j2n.bff_srv.controller.request.ProductRequest;
import com.example.j2n.bff_srv.controller.request.ProductScheduleRequest;
import com.example.j2n.bff_srv.service.ProductService;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.swagger.annotation.J2NApiResponse;
import com.example.j2n.swagger.annotation.J2NApiResponses;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/bff/product")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Endpoints for product and product management")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get all categories")
    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getAllCategories() {
        return productService.getAllCategories();
    }

    @Operation(summary = "Get category by ID")
    @GetMapping(value = "/categories/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getCategoryById(@PathVariable Long categoryId) {
        return productService.getCategoryById(categoryId);
    }

    @Operation(summary = "Get category by slug")
    @GetMapping(value = "/categories/slug/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getCategoryBySlug(@PathVariable String slug) {
        return productService.getCategoryBySlug(slug);
    }

    @Operation(summary = "Get category by name")
    @GetMapping(value = "/categories/name/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getCategoryByName(@PathVariable String name) {
        return productService.getCategoryByName(name);
    }

    @Operation(summary = "Create a new category")
    @PostMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 201, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> createCategory(@Valid @RequestBody CategoryRequest input) {
        return productService.createCategory(input);
    }

    @Operation(summary = "Update an existing category")
    @PutMapping(value = "/categories/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryRequest input) {
        return productService.updateCategory(categoryId, input);
    }

    @Operation(summary = "Delete a category")
    @DeleteMapping(value = "/categories/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 204, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> deleteCategory(@PathVariable Long categoryId) {
        return productService.deleteCategory(categoryId);
    }

    // --- Product Endpoints ---

    @Operation(summary = "Get all products")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getAllProducts() {
        return productService.getAllProducts();
    }

    @Operation(summary = "Get product by ID")
    @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getProductById(@PathVariable Long productId) {
        return productService.getProductById(productId);
    }

    @Operation(summary = "Get products by category ID")
    @GetMapping(value = "/category/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getProductsByCategory(@PathVariable Long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

    @Operation(summary = "Create a new product")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 201, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> createProduct(@Valid @RequestBody ProductRequest input) {
        return productService.createProduct(input);
    }

    @Operation(summary = "Update an existing product")
    @PutMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductRequest input) {
        return productService.updateProduct(productId, input);
    }

    @Operation(summary = "Delete a product")
    @DeleteMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 204, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> deleteProduct(@PathVariable Long productId) {
        return productService.deleteProduct(productId);
    }

    // --- Product Image Endpoints ---

    @Operation(summary = "Get images by product ID")
    @GetMapping(value = "/images/product/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getImagesByProductId(@PathVariable Long productId) {
        return productService.getImagesByProductId(productId);
    }

    @Operation(summary = "Add image to product")
    @PostMapping(value = "/images", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 201, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> addImageToProduct(@Valid @RequestBody ProductImageRequest input) {
        return productService.addImageToProduct(input);
    }

    @Operation(summary = "Delete product image")
    @DeleteMapping(value = "/images/{imageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 204, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> deleteProductImage(@PathVariable Long imageId) {
        return productService.deleteProductImage(imageId);
    }

    @Operation(summary = "Set primary image for product")
    @PatchMapping(value = "/images/{imageId}/primary", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> setPrimaryImage(@PathVariable Long imageId) {
        return productService.setPrimaryImage(imageId);
    }

    // --- Product Schedule Endpoints ---

    @Operation(summary = "Get schedules by product ID")
    @GetMapping(value = "/schedules/product/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getSchedulesByProductId(@PathVariable Long productId) {
        return productService.getSchedulesByProductId(productId);
    }

    @Operation(summary = "Add schedule to product")
    @PostMapping(value = "/schedules", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 201, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> addScheduleToProduct(@Valid @RequestBody ProductScheduleRequest input) {
        return productService.addScheduleToProduct(input);
    }

    @Operation(summary = "Update product schedule")
    @PutMapping(value = "/schedules/{scheduleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> updateProductSchedule(@PathVariable Long scheduleId,
            @Valid @RequestBody ProductScheduleRequest input) {
        return productService.updateProductSchedule(scheduleId, input);
    }

    @Operation(summary = "Delete product schedule")
    @DeleteMapping(value = "/schedules/{scheduleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 204, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> deleteProductSchedule(@PathVariable Long scheduleId) {
        return productService.deleteProductSchedule(scheduleId);
    }
}
