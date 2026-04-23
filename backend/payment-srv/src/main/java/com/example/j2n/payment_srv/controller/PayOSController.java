package com.example.j2n.payment_srv.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.payment_srv.service.PayOSService;
import com.example.j2n.utils.ResponseFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/payment/payos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "PayOS API", description = "Endpoints for PayOS specific operations")
public class PayOSController {

    private final PayOSService payOSService;

    @Operation(summary = "PayOS Webhook", description = "Endpoint to receive payment notifications from PayOS")
    @PostMapping("/webhook")
    public BaseResponse<String> handleWebhook(@RequestBody Object body) {
        log.info("Received PayOS webhook: {}", body);
        // Logic to verify and process webhook will go here
        return ResponseFactory.success("Webhook received");
    }
}
