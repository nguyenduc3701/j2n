package com.example.j2n.product_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.product_srv.dto.ProductDto;
import com.example.j2n.product_srv.service.ProductService;
import com.example.j2n.product_srv.service.response.ProductResponse;
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
public class ProductGraphQLController {

    private final ProductService productService;

    // --- Queries ---

    @QueryMapping(name = "getAllProducts")
    public BaseResponse<List<ProductResponse>> getAllProducts() {
        return productService.getAllProducts();
    }

    @QueryMapping(name = "getProductById")
    public BaseResponse<ProductResponse> getProductById(@Argument Long id) {
        return productService.getProductById(id);
    }

    @QueryMapping(name = "getProductsByCategory")
    public BaseResponse<List<ProductResponse>> getProductsByCategory(@Argument Long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

    @QueryMapping(name = "getProductsByType")
    public BaseResponse<List<ProductResponse>> getProductsByType(@Argument String type) {
        return productService.getProductsByType(type);
    }

    // --- Mutations ---

    @MutationMapping(name = "createProduct")
    public BaseResponse<ProductResponse> createProduct(@Argument @Valid ProductDto input) {
        return productService.createProduct(input);
    }

    @MutationMapping(name = "updateProduct")
    public BaseResponse<ProductResponse> updateProduct(@Argument Long id, @Argument @Valid ProductDto input) {
        return productService.updateProduct(id, input);
    }

    @MutationMapping(name = "deleteProduct")
    public BaseResponse<Boolean> deleteProduct(@Argument Long id) {
        return productService.deleteProduct(id);
    }
}
