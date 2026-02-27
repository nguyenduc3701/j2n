package com.example.j2n.bff_srv.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.utils.RestClientUtil;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private RestClientUtil restClientUtil;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getDashboardReport_Success() {
        // Arrange
        Object expectedResponse = new Object();
        when(restClientUtil.request(
                eq(GatewayPath.REPORT_MANAGEMENT_DASHBOARD_PATH),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class))).thenReturn(expectedResponse);

        // Act
        Object result = reportService.getDashboardReport();

        // Assert
        assertNotNull(result);
    }
}
