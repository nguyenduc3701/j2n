package com.example.j2n.product_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.product_srv.dto.ProductImageDto;
import com.example.j2n.product_srv.repository.entity.ProductImageEntity;
import com.example.j2n.product_srv.service.ProductImageService;
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
public class ProductImageGraphQLController {

    private final ProductImageService productImageService;

    // --- Queries ---

    @QueryMapping(name = "getImagesByProductId")
    public BaseResponse<List<ProductImageEntity>> getImagesByProductId(@Argument Long productId) {
        return productImageService.getImagesByProductId(productId);
    }

    // --- Mutations ---

    @MutationMapping(name = "addImageToProduct")
    public BaseResponse<ProductImageEntity> addImageToProduct(@Argument @Valid ProductImageDto input) {
        return productImageService.addImageToProduct(input);
    }

    @MutationMapping(name = "deleteProductImage")
    public BaseResponse<Boolean> deleteProductImage(@Argument Long id) {
        return productImageService.deleteProductImage(id);
    }

    @MutationMapping(name = "setPrimaryImage")
    public BaseResponse<ProductImageEntity> setPrimaryImage(@Argument Long id) {
        return productImageService.setPrimaryImage(id);
    }
}
