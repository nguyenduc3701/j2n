package com.example.j2n.room_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.room_srv.controller.request.BillRequest;
import com.example.j2n.room_srv.controller.request.CalculateAllBillsRequest;
import com.example.j2n.room_srv.controller.request.SearchBillsRequest;
import com.example.j2n.room_srv.controller.request.SearchBillsAdminRequest;
import com.example.j2n.room_srv.service.response.SearchBillsResponse;
import com.example.j2n.room_srv.repository.entity.BillEntity;
import com.example.j2n.room_srv.service.response.BillResponse;
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

        @PostMapping("/search")
        @Operation(summary = "Search bills", description = "Retrieve a list of bills matching the search criteria")
        @J2NApiResponses({
                        @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
        })
        public ResponseEntity<BaseResponse<SearchBillsResponse>> searchBills(
                        @Valid @RequestBody SearchBillsRequest request) {
                return ResponseEntity.ok(billingService.searchBills(request));
        }

        @PostMapping("/admin/search")
        @Operation(summary = "Search all bills (Admin)", description = "Retrieve a list of all bills matching the search criteria")
        @J2NApiResponses({
                        @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
        })
        public ResponseEntity<BaseResponse<SearchBillsResponse>> searchBillsAdmin(
                        @Valid @RequestBody SearchBillsAdminRequest request) {
                return ResponseEntity.ok(billingService.searchBillsAdmin(request));
        }

        @PostMapping("/calculate")
        @Operation(summary = "Calculate and create monthly bill for a specific room", description = "Calculate usage and generate a bill for a specific room")
        @J2NApiResponses({
                        @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
        })
        public ResponseEntity<BaseResponse<BillResponse>> calculateBillByRoomId(@Valid @RequestBody BillRequest request) {
                return ResponseEntity.ok(billingService.calculateBill(request));
        }

        @PostMapping("/calculate-all")
        @Operation(summary = "Calculate and create monthly bills for all rooms", description = "Generate bills for all occupied rooms for a specific month, using the provided new electricity indices per room")
        @J2NApiResponses({
                        @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
        })
        public ResponseEntity<BaseResponse<List<BillResponse>>> calculateAllBills(
                        @Valid @RequestBody CalculateAllBillsRequest request) {
                return ResponseEntity.ok(billingService.calculateAllBills(request));
        }

        @GetMapping("/room/{roomId}")
        @Operation(summary = "Get bills for a specific room", description = "Retrieve a list of bills associated with a room ID")
        @J2NApiResponses({
                        @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
        })
        public ResponseEntity<BaseResponse<List<BillResponse>>> getBillsByRoomId(@PathVariable Long roomId) {
                return ResponseEntity.ok(billingService.getBillsByRoomId(roomId));
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
