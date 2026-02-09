package com.example.j2n.report_srv.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.report_srv.constant.MessageEnum;
import com.example.j2n.report_srv.messaging.user.event.UserRegisteredEvent;
import com.example.j2n.report_srv.repository.DistributionChartRepository;
import com.example.j2n.report_srv.repository.SummaryMetricsRepository;
import com.example.j2n.report_srv.repository.entity.DistributionChart;
import com.example.j2n.report_srv.repository.entity.SummaryMetrics;
import com.example.j2n.utils.ResponseFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagementService {
    private final static String TOTAL_USERS_METRIC_KEY = "total_users";
    private final static String ACCOUNT_CATEGORY = "ACCOUNT";
    private final static String USER_TYPE_CHART = "USER_TYPE";

    private final SummaryMetricsRepository summaryMetricsRepository;
    private final DistributionChartRepository distributionChartRepository;

    @Transactional
    public BaseResponse<Object> handleUserRegistrationReport(UserRegisteredEvent event) {
        log.info("[REPORT-SRV] Processing registration report for user: {}", event.getUserId());
        try {
            updateSummaryMetrics(event);
            updateDistributionChart(event);
            log.info("[REPORT-SRV] Successfully updated all report tables for user: {}", event.getUserId());
            return ResponseFactory.of(MessageEnum.UPDATE_USER_REPORT_SUCCESS, null);
        } catch (Exception e) {
            log.error("[REPORT-SRV] Failed to update report for user: {}. Error: {}", event.getUserId(),
                    e.getMessage());
            throw e;
        }
    }

    private void updateSummaryMetrics(UserRegisteredEvent event) {
        SummaryMetrics summary = summaryMetricsRepository.findById(TOTAL_USERS_METRIC_KEY)
                .orElse(buildSummaryMetricsEntity(event));
        if (summary.getUpdatedAt() != null) {
            log.info("[REPORT-SRV] Updating summary metrics for user: {}", event.getUserId());
            summary.setMetricValue(summary.getMetricValue() + 1);
        }
        summaryMetricsRepository.save(summary);
    }

    private void updateDistributionChart(UserRegisteredEvent event) {
        DistributionChart chart = distributionChartRepository
                .findByChartTypeAndItemLabel(USER_TYPE_CHART, event.getRole())
                .orElse(buildDistributionChartEntity(event));

        if (chart.getItemValue() != null) {
            log.info("[REPORT-SRV] Updating distribution chart for user: {}", event.getUserId());
            chart.setItemValue(chart.getItemValue() + 1);
        }
        distributionChartRepository.save(chart);
    }

    private SummaryMetrics buildSummaryMetricsEntity(UserRegisteredEvent event) {
        log.info("[REPORT-SRV] Building summary metrics for user: {}", event.getUserId());
        SummaryMetrics summaryMetrics = new SummaryMetrics();
        summaryMetrics.setMetricKey(TOTAL_USERS_METRIC_KEY);
        summaryMetrics.setCategory(ACCOUNT_CATEGORY);
        summaryMetrics.setMetricValue(1L);
        return summaryMetrics;
    }

    private DistributionChart buildDistributionChartEntity(UserRegisteredEvent event) {
        log.info("[REPORT-SRV] Building distribution chart for user: {}", event.getUserId());
        DistributionChart distributionChart = new DistributionChart();
        distributionChart.setChartType(USER_TYPE_CHART);
        distributionChart.setItemLabel(event.getRole());
        distributionChart.setItemValue(1L);
        return distributionChart;
    }
}