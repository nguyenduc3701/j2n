package com.example.j2n.report_srv.constant;

import java.util.Map;

public class ReportApiMapping {
    public static final Map<String, String> API_MAP = Map.of(
            "/api/report/management/dashboard", "getDashboardReport");

    public static String getMethodName(String endpoint) {
        return API_MAP.get(endpoint);
    }
}
