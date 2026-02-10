package com.example.j2n.report_srv.service.response;

import java.util.List;

import com.example.j2n.report_srv.repository.entity.ActivePromotion;
import com.example.j2n.report_srv.repository.entity.DistributionChart;
import com.example.j2n.report_srv.repository.entity.MonthlyFinancials;
import com.example.j2n.report_srv.repository.entity.RoomUtilityReport;
import com.example.j2n.report_srv.repository.entity.SummaryMetrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardReportResponse {
    private List<SummaryMetrics> summaryMetrics;
    private List<MonthlyFinancials> monthlyFinancials;
    private List<RoomUtilityReport> roomUtilityReports;
    private List<DistributionChart> distributionCharts;
    private List<ActivePromotion> activePromotions;
}
