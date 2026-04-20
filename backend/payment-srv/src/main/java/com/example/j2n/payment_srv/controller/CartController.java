package com.example.j2n.payment_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.payment_srv.repository.entity.CartItemEntity;
import com.example.j2n.payment_srv.service.CartService;
import com.example.j2n.swagger.annotation.J2NApiExample;
import com.example.j2n.swagger.annotation.J2NApiResponse;
import com.example.j2n.swagger.annotation.J2NApiResponses;
import com.example.j2n.utils.ResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.j2n.enums.BaseMessageEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.j2n.payment_srv.dto.request.CartItemRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/payment/carts")
@RequiredArgsConstructor
@Tag(name = "Cart API", description = "Endpoints for managing shopping cart items")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Get all cart items", description = "Retrieve all items currently stored in the shopping cart system across all users.")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS, summary = "Successfully retrieved all cart items")
            })
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<CartItemEntity>>> getAllCartItems() {
        return ResponseEntity.ok(cartService.getAllCartItems());
    }

    @Operation(summary = "Get cart items by user ID", description = "Retrieve a list of shopping cart items for a specific user.")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS, summary = "Successfully retrieved items for the user")
            }),
            @J2NApiResponse(httpCode = 400, description = "Not Found", examples = {
                    @J2NApiExample(status = "CART_NOT_FOUND", args = {
                            "userId" }, summary = "No items found in the user's cart")
            })
    })
    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<CartItemEntity>>> getCartByUserId(
            @Parameter(description = "The unique identifier of the user", example = "user-123") @PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @Operation(summary = "Add item to cart", description = "Add a new item to the user's cart or increase the quantity if it already exists. Metadata is updated if provided in the request.")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS, summary = "Item successfully added/updated in cart")
            })
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<CartItemEntity>> addToCart(@Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addToCart(request));
    }

    @Operation(summary = "Delete cart items", description = "Remove one or more items from the user's cart by their unique item IDs.")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS, summary = "Specified items successfully removed from cart")
            }),
            @J2NApiResponse(httpCode = 400, description = "Invalid Input", examples = {
                    @J2NApiExample(status = "DELETE_CART_ITEMS_SHOULD_NOT_BE_EMPTY", summary = "The list of item IDs to delete is empty or null")
            })
    })
    @DeleteMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> deleteCartItems(
            @Parameter(description = "The unique identifier of the user", example = "user-123") @PathVariable String userId,
            @Parameter(description = "List of cart item database IDs to be removed", example = "[1, 2, 3]") @RequestParam List<Long> ids) {
        return ResponseEntity.ok(cartService.deleteCartItems(userId, ids));
    }
}
