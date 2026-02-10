package com.example.j2n.report_srv.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.report_srv.service.ManagementService;
import com.example.j2n.report_srv.service.response.DashboardReportResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/report/management")
@RequiredArgsConstructor
public class ManagementController {

    private final ManagementService managementService;

    @GetMapping("/dashboard")
    public BaseResponse<DashboardReportResponse> getDashboardReport() {
        return managementService.getDashboardReport();
    }
}
