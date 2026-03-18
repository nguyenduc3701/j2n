package com.example.j2n.bff_srv.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.j2n.bff_srv.service.ReportService;
import com.example.j2n.swagger.annotation.*;
import com.example.j2n.enums.BaseMessageEnum;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bff/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard report", description = "Fetch statistical report for the dashboard")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> getDashboardReport() {
        return ResponseEntity.ok(reportService.getDashboardReport());
    }
}
