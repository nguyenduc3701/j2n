package com.example.j2n.payment_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.payment_srv.repository.entity.CartItemEntity;
import com.example.j2n.payment_srv.service.CartService;
import com.example.j2n.swagger.annotation.J2NApiExample;
import com.example.j2n.swagger.annotation.J2NApiResponse;
import com.example.j2n.swagger.annotation.J2NApiResponses;
import com.example.j2n.utils.ResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.j2n.enums.BaseMessageEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payment/carts")
@RequiredArgsConstructor
@Tag(name = "Cart API", description = "Endpoints for managing shopping cart items")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Get all cart items", description = "Get all cart items")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<CartItemEntity>>> getAllCartItems() {
        return ResponseEntity.ok(cartService.getAllCartItems());
    }
}
