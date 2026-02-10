package com.example.j2n.bff_srv.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.j2n.bff_srv.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bff/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    public ResponseEntity<Object> getDashboardReport() {
        return ResponseEntity.ok(reportService.getDashboardReport());
    }
}
