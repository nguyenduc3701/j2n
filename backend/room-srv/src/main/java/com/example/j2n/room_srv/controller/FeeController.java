package com.example.j2n.room_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.room_srv.constant.MessageEnum;
import com.example.j2n.room_srv.controller.request.CreateFeeRequest;
import com.example.j2n.room_srv.controller.request.UpdateFeeRequest;
import com.example.j2n.room_srv.service.FeeService;
import com.example.j2n.room_srv.service.response.FeeResponse;
import com.example.j2n.swagger.annotation.*;
import com.example.j2n.utils.ResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fees")
@RequiredArgsConstructor
@Tag(name = "Fee Controller", description = "Endpoints for managing fees")
public class FeeController {

        private final FeeService feeService;

        @GetMapping
        @Operation(summary = "Get all active fees")
        @J2NApiResponses({
                        @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
        })
        public ResponseEntity<BaseResponse<List<FeeResponse>>> getActiveFees() {
                return ResponseEntity.ok(ResponseFactory.success(feeService.getActiveFees()));
        }

        @PostMapping
        @Operation(summary = "Create fee configuration", description = "Create a new fee type")
        @J2NApiResponses({
                        @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
        })
        public ResponseEntity<BaseResponse<FeeResponse>> createFee(
                        @Valid @RequestBody CreateFeeRequest request) {
                return ResponseEntity.ok(feeService.createFee(request));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update fee configuration", description = "Update specific fields of a fee")
        @J2NApiResponses({
                        @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS),
                        @J2NApiResponse(httpCode = 400, description = BaseMessageEnum.BaseMessageConstants.BAD_REQUEST, examples = {
                                         @J2NApiExample(status = MessageEnum.MessageConstants.FEE_NOT_FOUND)
                        })
        })
        public ResponseEntity<BaseResponse<FeeResponse>> updateFee(
                        @PathVariable Long id, @Valid @RequestBody UpdateFeeRequest request) {
                return ResponseEntity.ok(feeService.updateFee(id, request));
        }
}

