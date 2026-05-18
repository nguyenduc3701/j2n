package com.example.j2n.bff_srv.controller;

import com.example.j2n.bff_srv.controller.request.OrderItemRequest;
import com.example.j2n.bff_srv.controller.request.UpdateOrderItemRequest;
import com.example.j2n.bff_srv.service.OrderService;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.swagger.annotation.J2NApiExample;
import com.example.j2n.swagger.annotation.J2NApiResponse;
import com.example.j2n.swagger.annotation.J2NApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bff/order")
@RequiredArgsConstructor
@Tag(name = "BFF Order Controller", description = "BFF Endpoints for managing items in the order/cart")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Get all cart items", description = "Retrieve all items currently stored in the order system across all users.")
    @GetMapping(value = "/carts", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS, summary = "Successfully retrieved all cart items")
            })
    })
    public ResponseEntity<Object> getAllCartItems() {
        return ResponseEntity.ok(orderService.getAllOrderItems());
    }

    @Operation(summary = "Get all order items", description = "Retrieve all items currently stored in the order system across all users.")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS, summary = "Successfully retrieved all order items")
            })
    })
    public ResponseEntity<Object> getAllOrderItems() {
        return ResponseEntity.ok(orderService.getAllOrderItems());
    }

    @Operation(summary = "Get order items by user ID", description = "Retrieve all items currently in the user's order.")
    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS, summary = "Successfully retrieved order items for the user")
            })
    })
    public ResponseEntity<Object> getOrderByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.getOrderByUserId(userId));
    }

    @Operation(summary = "Add an item to order", description = "Add a new item or increase the quantity of an existing item in the order.")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS, summary = "Successfully added item to order")
            })
    })
    public ResponseEntity<Object> addToOrder(@Valid @RequestBody OrderItemRequest request) {
        return ResponseEntity.ok(orderService.addToOrder(request));
    }

    @Operation(summary = "Update item quantity in order", description = "Update the quantity of an existing item in the user's order.")
    @PutMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS, summary = "Successfully updated item quantity")
            })
    })
    public ResponseEntity<Object> updateQuantity(
            @PathVariable String userId, @Valid @RequestBody UpdateOrderItemRequest request) {
        return ResponseEntity.ok(orderService.updateQuantity(userId, request));
    }

    @Operation(summary = "Delete items from order", description = "Remove one or more items from the user's order.")
    @DeleteMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS, summary = "Successfully deleted items from order")
            })
    })
    public ResponseEntity<Object> deleteOrderItems(
            @PathVariable String userId, @RequestParam List<Long> ids) {
        return ResponseEntity.ok(orderService.deleteOrderItems(userId, ids));
    }
}
