package com.example.j2n.room_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.room_srv.controller.request.BillRequest;
import com.example.j2n.room_srv.repository.entity.BillEntity;
import com.example.j2n.room_srv.service.BillingService;
import com.example.j2n.room_srv.service.PaymentService;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.room_srv.constant.MessageEnum;
import com.example.j2n.swagger.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
@Tag(name = "Bill Controller", description = "Endpoints for managing bills and utility indices")
public class BillController {

    private final BillingService billingService;
    private final PaymentService paymentService;

    @PostMapping("/calculate")
    @Operation(summary = "Calculate and create monthly bill", description = "Calculate usage and generate a bill for the current month")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public ResponseEntity<BaseResponse<BillEntity>> calculateBill(@Valid @RequestBody BillRequest request) {
        return ResponseEntity.ok(billingService.calculateBill(request));
    }


    @GetMapping("/room/{roomId}")
    @Operation(summary = "Get bills for a specific room", description = "Retrieve a list of bills associated with a room ID")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public ResponseEntity<BaseResponse<List<BillEntity>>> getBillsByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(billingService.getBillsByRoom(roomId));
    }

    @PostMapping("/{billId}/pay")
    @Operation(summary = "Initiate payment for a bill", description = "Generate a payment link for the specified bill")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS),
            @J2NApiResponse(httpCode = 400, description = BaseMessageEnum.BaseMessageConstants.BAD_REQUEST, examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.BILL_NOT_FOUND)
            })
    })
    public ResponseEntity<BaseResponse<Object>> payBill(@PathVariable String billId) {
        return ResponseEntity.ok(paymentService.initiatePayment(billId));
    }
}
