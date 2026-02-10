package com.example.j2n.bff_srv.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.utils.RestClientUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final RestClientUtil restClientUtil;

    public Object getDashboardReport() {
        return restClientUtil.request(GatewayPath.REPORT_MANAGEMENT_DASHBOARD_PATH, HttpMethod.GET, null,
                new ParameterizedTypeReference<Object>() {
                });
    }

}
