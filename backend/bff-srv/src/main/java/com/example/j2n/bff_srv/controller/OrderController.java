package com.example.j2n.bff_srv.controller;

import com.example.j2n.bff_srv.service.OrderService;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.swagger.annotation.J2NApiResponse;
import com.example.j2n.swagger.annotation.J2NApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bff/order")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "Endpoints for cart and order management")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Get all cart items")
    @GetMapping(value = "/carts", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Object getAllCartItems() {
        return orderService.getAllCartItems();
    }
}
