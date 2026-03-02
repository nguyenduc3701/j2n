package com.example.j2n.report_srv.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import com.example.j2n.constants.CommonConst;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.lib.proto.*;
import com.example.j2n.report_srv.constant.MessageEnum;
import com.example.j2n.report_srv.constant.ReportApiMapping;
import com.example.j2n.report_srv.interceptor.GrpcServerAuthInterceptor;
import com.example.j2n.report_srv.messaging.user.event.UserRegisteredEvent;
import com.example.j2n.report_srv.repository.DistributionChartRepository;
import com.example.j2n.report_srv.repository.SummaryMetricsRepository;
import com.example.j2n.report_srv.repository.entity.DistributionChart;
import com.example.j2n.report_srv.repository.entity.SummaryMetrics;
import com.example.j2n.report_srv.repository.ActivePromotionRepository;
import com.example.j2n.report_srv.repository.MonthlyFinancialsRepository;
import com.example.j2n.report_srv.repository.RoomUtilityReportRepository;
import com.example.j2n.report_srv.repository.entity.ActivePromotion;
import com.example.j2n.report_srv.repository.entity.MonthlyFinancials;
import com.example.j2n.report_srv.repository.entity.RoomUtilityReport;
import com.example.j2n.report_srv.service.response.DashboardReportResponse;
import com.example.j2n.utils.ResponseFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Empty;
import com.example.j2n.report_srv.utils.GrpcResponseFactory;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ManagementService extends ReportServiceGrpc.ReportServiceImplBase {
    private final static String TOTAL_USERS_METRIC_KEY = "total_users";
    private final static String ACCOUNT_CATEGORY = "ACCOUNT";
    private final static String USER_TYPE_CHART = "USER_TYPE";

    private final SummaryMetricsRepository summaryMetricsRepository;
    private final DistributionChartRepository distributionChartRepository;
    private final MonthlyFinancialsRepository monthlyFinancialsRepository;
    private final RoomUtilityReportRepository roomUtilityReportRepository;
    private final ActivePromotionRepository activePromotionRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void getDashboardReport(GetDashboardRequest request, StreamObserver<BaseProtoResponse> responseObserver) {
        String userId = GrpcServerAuthInterceptor.USER_ID_CTX.get();
        log.info("[GRPC] Requesting dashboard report for user: {}", userId);
        BaseResponse<DashboardReportResponse> baseResponse = this.getDashboardReportResponse();
        GrpcResponseFactory.of(responseObserver, baseResponse, objectMapper);
    }

    @Override
    public void getApiCatalog(Empty request, StreamObserver<ApiCatalogResponse> responseObserver) {
        log.info("[GRPC] Requesting API catalog");
        ApiCatalogResponse response = ApiCatalogResponse.newBuilder()
                .putAllMappings(ReportApiMapping.API_MAP)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Cacheable(value = CommonConst.DASHBOARD_CACHE_KEY, key = CommonConst.MAIN_REPORT_KEY)
    public BaseResponse<DashboardReportResponse> getDashboardReportResponse() {
        log.info("[REPORT-SRV] Starting to get dashboard report");

        CompletableFuture<List<SummaryMetrics>> summaryTask = getSummaryMetricsAsync();
        CompletableFuture<List<DistributionChart>> distributionTask = getDistributionChartsAsync();
        CompletableFuture<List<MonthlyFinancials>> monthlyTask = getMonthlyFinancialsAsync();
        CompletableFuture<List<RoomUtilityReport>> utilityTask = getRoomUtilityReportsAsync();
        CompletableFuture<List<ActivePromotion>> promotionTask = getActivePromotionsAsync();

        CompletableFuture.allOf(summaryTask, distributionTask, monthlyTask, utilityTask, promotionTask).join();

        DashboardReportResponse response = DashboardReportResponse.builder()
                .summaryMetrics(summaryTask.join())
                .distributionCharts(distributionTask.join())
                .monthlyFinancials(monthlyTask.join())
                .roomUtilityReports(utilityTask.join())
                .activePromotions(promotionTask.join())
                .build();
        log.info("[REPORT-SRV] Finished to get dashboard report");
        return ResponseFactory.of(BaseMessageEnum.SUCCESS, response);
    }

    private CompletableFuture<List<SummaryMetrics>> getSummaryMetricsAsync() {
        return CompletableFuture.supplyAsync(summaryMetricsRepository::findAll)
                .exceptionally(ex -> {
                    log.error("[REPORT-SRV] Error getting summary metrics", ex);
                    return List.of();
                });
    }

    private CompletableFuture<List<DistributionChart>> getDistributionChartsAsync() {
        return CompletableFuture.supplyAsync(distributionChartRepository::findAll)
                .exceptionally(ex -> {
                    log.error("[REPORT-SRV] Error getting distribution charts", ex);
                    return List.of();
                });
    }

    private CompletableFuture<List<MonthlyFinancials>> getMonthlyFinancialsAsync() {
        return CompletableFuture.supplyAsync(monthlyFinancialsRepository::findAll)
                .exceptionally(ex -> {
                    log.error("[REPORT-SRV] Error getting monthly financials", ex);
                    return List.of();
                });
    }

    private CompletableFuture<List<RoomUtilityReport>> getRoomUtilityReportsAsync() {
        return CompletableFuture.supplyAsync(roomUtilityReportRepository::findAll)
                .exceptionally(ex -> {
                    log.error("[REPORT-SRV] Error getting room utility reports", ex);
                    return List.of();
                });
    }

    private CompletableFuture<List<ActivePromotion>> getActivePromotionsAsync() {
        return CompletableFuture.supplyAsync(activePromotionRepository::findAll)
                .exceptionally(ex -> {
                    log.error("[REPORT-SRV] Error getting active promotions", ex);
                    return List.of();
                });
    }

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
        SummaryMetrics summary = findOrCreateSummaryMetricsById(TOTAL_USERS_METRIC_KEY);
        if (summary == null) {
            log.info("[REPORT-SRV] Creating summary metrics for user: {}", event.getUserId());
            summary = buildSummaryMetricsEntity(event);
        } else {
            log.info("[REPORT-SRV] Updating summary metrics for user: {}", event.getUserId());
            summary.setMetricValue(summary.getMetricValue() + 1);
        }
        summaryMetricsRepository.save(summary);
    }

    private void updateDistributionChart(UserRegisteredEvent event) {
        DistributionChart chart = findOrCreateDistributionChart(USER_TYPE_CHART, event.getRole());
        if (chart == null) {
            log.info("[REPORT-SRV] Creating distribution chart for user: {}", event.getUserId());
            chart = buildDistributionChartEntity(event);
        } else {
            log.info("[REPORT-SRV] Updating distribution chart for user: {}", event.getUserId());
            chart.setItemValue(chart.getItemValue() + 1);
        }
        distributionChartRepository.save(chart);
    }

    private SummaryMetrics findOrCreateSummaryMetricsById(String metricId) {
        return summaryMetricsRepository.findById(metricId)
                .orElse(null);
    }

    private DistributionChart findOrCreateDistributionChart(String chartType, String itemLabel) {
        return distributionChartRepository.findByChartTypeAndItemLabel(chartType, itemLabel)
                .orElse(null);
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