package com.example.j2n.bff_srv.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.j2n.bff_srv.service.ReportService;
import com.example.j2n.bff_srv.interceptor.JwtDecodeInterceptor;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private JwtDecodeInterceptor jwtDecodeInterceptor;

    @BeforeEach
    void setUp() throws Exception {
        when(jwtDecodeInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Test
    @WithMockUser
    void getDashboardReport_ShouldReturnOk() throws Exception {
        // Arrange
        // Using a Map instead of Object() to ensure Jackson can serialize it
        Object dummyResponse = Collections.singletonMap("key", "value");
        when(reportService.getDashboardReport()).thenReturn(dummyResponse);

        // Act & Assert
        mockMvc.perform(get("/api/bff/report/dashboard"))
                .andExpect(status().isOk());
    }
}
