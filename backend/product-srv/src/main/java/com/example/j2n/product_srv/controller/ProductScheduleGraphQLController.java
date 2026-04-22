package com.example.j2n.product_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.product_srv.dto.ProductScheduleDto;
import com.example.j2n.product_srv.repository.entity.ProductScheduleEntity;
import com.example.j2n.product_srv.service.ProductScheduleService;
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
public class ProductScheduleGraphQLController {

    private final ProductScheduleService productScheduleService;

    // --- Queries ---

    @QueryMapping(name = "getSchedulesByProductId")
    public BaseResponse<List<ProductScheduleEntity>> getSchedulesByProductId(@Argument Long productId) {
        return productScheduleService.getSchedulesByProductId(productId);
    }

    // --- Mutations ---

    @MutationMapping(name = "addScheduleToProduct")
    public BaseResponse<ProductScheduleEntity> addScheduleToProduct(@Argument @Valid ProductScheduleDto input) {
        return productScheduleService.addScheduleToProduct(input);
    }

    @MutationMapping(name = "updateProductSchedule")
    public BaseResponse<ProductScheduleEntity> updateProductSchedule(@Argument Long id, @Argument @Valid ProductScheduleDto input) {
        return productScheduleService.updateProductSchedule(id, input);
    }

    @MutationMapping(name = "deleteProductSchedule")
    public BaseResponse<Boolean> deleteProductSchedule(@Argument Long id) {
        return productScheduleService.deleteProductSchedule(id);
    }
}
