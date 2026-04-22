package com.example.j2n.payment_srv.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.payment_srv.repository.entity.TransactionEntity;
import com.example.j2n.payment_srv.service.TransactionService;
import com.example.j2n.swagger.annotation.J2NApiExample;
import com.example.j2n.swagger.annotation.J2NApiResponse;
import com.example.j2n.swagger.annotation.J2NApiResponses;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payment/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction API", description = "Endpoints for managing payment transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Get all transactions", description = "Get all transactions")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<TransactionEntity>>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }
}
