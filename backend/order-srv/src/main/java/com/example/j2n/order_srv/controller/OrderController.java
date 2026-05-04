package com.example.j2n.order_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.order_srv.controller.request.OrderItemRequest;
import com.example.j2n.order_srv.controller.request.UpdateOrderItemRequest;
import com.example.j2n.order_srv.repository.entity.OrderItemEntity;
import com.example.j2n.order_srv.service.OrderService;
import com.example.j2n.order_srv.service.response.OrderItemWithProductResponse;
import com.example.j2n.swagger.annotation.J2NApiExample;
import com.example.j2n.swagger.annotation.J2NApiResponse;
import com.example.j2n.swagger.annotation.J2NApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order Controller", description = "Endpoints for managing items in the order")
public class OrderController {

        private final OrderService orderService;

        @Operation(summary = "Get all order items", description = "Retrieve all items currently stored in the order system across all users.")
        @J2NApiResponses({
                        @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                                        @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS, summary = "Successfully retrieved all order items")
                        })
        })
        @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<BaseResponse<List<OrderItemWithProductResponse>>> getAllOrderItems() {
                return ResponseEntity.ok(orderService.getAllOrderItems());
        }

        @Operation(summary = "Get order items by user ID", description = "Retrieve all items currently in the user's order.")
        @J2NApiResponses({
                        @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                                        @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS, summary = "Successfully retrieved order items for the user")
                        })
        })
        @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<BaseResponse<List<OrderItemWithProductResponse>>> getOrderByUserId(
                        @PathVariable String userId) {
                return ResponseEntity.ok(orderService.getOrderByUserId(userId));
        }

        @Operation(summary = "Add an item to order", description = "Add a new item or increase the quantity of an existing item in the order.")
        @J2NApiResponses({
                        @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                                        @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS, summary = "Successfully added item to order")
                        })
        })
        @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<BaseResponse<OrderItemEntity>> addToOrder(@RequestBody OrderItemRequest request) {
                return ResponseEntity.ok(orderService.addToOrder(request));
        }

        @Operation(summary = "Update item quantity in order", description = "Update the quantity of an existing item in the user's order.")
        @J2NApiResponses({
                        @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                                        @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS, summary = "Successfully updated item quantity")
                        })
        })
        @PutMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<BaseResponse<OrderItemEntity>> updateQuantity(
                        @PathVariable String userId, @RequestBody UpdateOrderItemRequest request) {
                return ResponseEntity.ok(orderService.updateQuantity(request, userId));
        }

        @Operation(summary = "Delete items from order", description = "Remove one or more items from the user's order.")
        @J2NApiResponses({
                        @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                                        @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS, summary = "Successfully deleted items from order")
                        })
        })
        @DeleteMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<BaseResponse<Void>> deleteOrderItems(
                        @PathVariable String userId, @RequestParam List<Long> ids) {
                return ResponseEntity.ok(orderService.deleteOrderItems(userId, ids));
        }
}
