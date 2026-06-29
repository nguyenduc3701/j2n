package com.example.j2n.report_srv.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.constants.CommonConst;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.lib.proto.*;
import com.example.j2n.report_srv.constant.MessageEnum;
import com.example.j2n.report_srv.constant.ReportApiMapping;
import static com.example.j2n.report_srv.constant.ReportConstant.*;
import com.example.j2n.report_srv.messaging.payment.event.PaymentConfirmedEvent;
import com.example.j2n.report_srv.messaging.room.event.RoomStatusUpdatedEvent;
import com.example.j2n.report_srv.messaging.user.event.UserRegisteredEvent;
import com.example.j2n.report_srv.messaging.room.event.BillsCalculatedEvent;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ManagementService extends ReportServiceGrpc.ReportServiceImplBase {

    @Lazy
    @Autowired
    private ManagementService self;

    private final SummaryMetricsRepository summaryMetricsRepository;
    private final DistributionChartRepository distributionChartRepository;
    private final MonthlyFinancialsRepository monthlyFinancialsRepository;
    private final RoomUtilityReportRepository roomUtilityReportRepository;
    private final ActivePromotionRepository activePromotionRepository;
    private final ObjectMapper objectMapper;

    @Override
    @LogAround(message = "[GRPC] Requesting dashboard report")
    public void getDashboardReport(GetDashboardRequest request, StreamObserver<BaseProtoResponse> responseObserver) {
        BaseResponse<DashboardReportResponse> baseResponse = self.getDashboardReportResponse();
        GrpcResponseFactory.of(responseObserver, baseResponse, objectMapper);
    }

    @Override
    @LogAround(message = "[GRPC] Requesting API catalog")
    public void getApiCatalog(Empty request, StreamObserver<ApiCatalogResponse> responseObserver) {
        ApiCatalogResponse response = ApiCatalogResponse.newBuilder()
                .putAllMappings(ReportApiMapping.API_MAP)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Cacheable(value = CommonConst.DASHBOARD_CACHE_KEY, key = CommonConst.MAIN_REPORT_KEY)
    @LogAround(message = "[REPORT-SRV] Get dashboard report")
    public BaseResponse<DashboardReportResponse> getDashboardReportResponse() {
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
    @LogAround(message = "[REPORT-SRV] Processing registration report")
    public BaseResponse<Object> handleUserRegistrationReport(UserRegisteredEvent event) {
        try {
            if ("ACTIVE".equalsIgnoreCase(event.getStatus())) {
                updateSummaryMetric(ACTIVE_USERS_METRIC_KEY, ACCOUNT_CATEGORY, true);
            } else {
                updateSummaryMetric(INACTIVE_USERS_METRIC_KEY, ACCOUNT_CATEGORY, true);
            }
            updateDistributionChart(event);
            return ResponseFactory.of(MessageEnum.UPDATE_USER_REPORT_SUCCESS, null);
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    @LogAround(message = "[REPORT-SRV] Processing room status update report")
    public void handleRoomStatusUpdate(RoomStatusUpdatedEvent event) {
        log.info("[REPORT-SRV] Handling room status update: roomId={} old={} new={}", 
                event.getRoomId(), event.getOldStatus(), event.getNewStatus());
        
        boolean wasAvailable = STATUS_AVAILABLE.equalsIgnoreCase(event.getOldStatus());
        boolean isAvailable = STATUS_AVAILABLE.equalsIgnoreCase(event.getNewStatus());

        if (wasAvailable && !isAvailable) {
            // Room is no longer available (e.g. occupied) -> decrement empty rooms
            updateSummaryMetric(EMPTY_ROOMS_METRIC_KEY, ROOM_CATEGORY, false);
        } else if (!wasAvailable && isAvailable) {
            // Room became available -> increment empty rooms
            updateSummaryMetric(EMPTY_ROOMS_METRIC_KEY, ROOM_CATEGORY, true);
        }
    }

    @Transactional
    @LogAround(message = "[REPORT-SRV] Processing product report change")
    public BaseResponse<Object> handleProductReport(boolean isIncrement) {
        try {
            updateSummaryMetric(TOTAL_TOURS_METRIC_KEY, PRODUCT_CATEGORY, isIncrement);
            return ResponseFactory.of(BaseMessageEnum.SUCCESS, null);
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    @LogAround(message = "[REPORT-SRV] Processing payment confirmed report")
    public void handlePaymentConfirmed(PaymentConfirmedEvent event) {
        if (event.getAmount() == null || event.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("[REPORT-SRV] Skipping payment report — amount is zero or null for txId={}", event.getTransactionId());
            return;
        }
        String domain = "PRODUCT";
        String monthYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        MonthlyFinancials financials = monthlyFinancialsRepository
                .findByMonthYearAndDomain(monthYear, domain)
                .orElseGet(() -> MonthlyFinancials.builder()
                        .monthYear(monthYear)
                        .domain(domain)
                        .totalIncome(BigDecimal.ZERO)
                        .totalOrders(0)
                        .remainingAmount(BigDecimal.ZERO)
                        .build());

        financials.setTotalIncome(financials.getTotalIncome().add(event.getAmount()));
        financials.setTotalOrders(financials.getTotalOrders() + 1);
        monthlyFinancialsRepository.save(financials);

        log.info("[REPORT-SRV] Monthly financials updated: month={} domain={} income={} orders={}",
                monthYear, domain, financials.getTotalIncome(), financials.getTotalOrders());
    }

    @Transactional
    @LogAround(message = "[REPORT-SRV] Processing bills calculated report")
    public void handleBillsCalculated(BillsCalculatedEvent event) {
        log.info("[REPORT-SRV] Handling bills calculated: monthYear={} totalBills={} totalUnpaid={}", 
                event.getMonthYear(), event.getTotalBills(), event.getTotalUnpaidAmount());
        
        setSummaryMetricValue(TOTAL_BILLS_METRIC_KEY, FINANCE_CATEGORY, event.getTotalBills().longValue());
        setSummaryMetricValue(UNPAID_AMOUNT_METRIC_KEY, FINANCE_CATEGORY, event.getTotalUnpaidAmount().longValue());
        setSummaryMetricValue(ELECTRIC_AMOUNT_METRIC_KEY, UTILITY_CATEGORY, event.getTotalElectricityAmount().longValue());
        setSummaryMetricValue(WATER_AMOUNT_METRIC_KEY, UTILITY_CATEGORY, event.getTotalWaterAmount().longValue());

        updateRoomUtilityReport(event);
    }

    private void updateRoomUtilityReport(BillsCalculatedEvent event) {
        RoomUtilityReport report = roomUtilityReportRepository.findById(event.getMonthYear())
                .orElseGet(() -> RoomUtilityReport.builder()
                        .monthYear(event.getMonthYear())
                        .build());

        report.setTotalElectricity(event.getTotalElectricityAmount());
        report.setTotalWater(event.getTotalWaterAmount());
        roomUtilityReportRepository.save(report);

        log.info("[REPORT-SRV] Room utility report updated for {}", event.getMonthYear());
    }

    @LogAround(message = "[REPORT-SRV] Setting summary metric value")
    private void setSummaryMetricValue(String metricKey, String category, Long value) {
        int updatedRows = summaryMetricsRepository.updateValue(metricKey, value);
        if (updatedRows == 0) {
            SummaryMetrics summary = SummaryMetrics.builder()
                    .metricKey(metricKey)
                    .category(category)
                    .metricValue(value)
                    .build();
            summaryMetricsRepository.save(summary);
        }
    }

    @LogAround(message = "[REPORT-SRV] Updating summary metric")
    private void updateSummaryMetric(String metricKey, String category, boolean isIncrement) {
        int updatedRows = 0;
        if (isIncrement) {
            updatedRows = summaryMetricsRepository.incrementValue(metricKey);
        } else {
            updatedRows = summaryMetricsRepository.decrementValueIfGreaterThanZero(metricKey);
        }
        if (updatedRows == 0 && isIncrement) {
            try {
                SummaryMetrics summary = new SummaryMetrics();
                summary.setMetricKey(metricKey);
                summary.setCategory(category);
                summary.setMetricValue(1L);
                summaryMetricsRepository.save(summary);
            } catch (DataIntegrityViolationException e) {
                summaryMetricsRepository.incrementValue(metricKey);
            }
        }
    }

    @LogAround(message = "[REPORT-SRV] Updating distribution chart")
    private void updateDistributionChart(UserRegisteredEvent event) {
        DistributionChart chart = findOrCreateDistributionChart(USER_TYPE_CHART, event.getRole());
        if (chart == null) {
            chart = buildDistributionChartEntity(event);
        } else {
            chart.setItemValue(chart.getItemValue() + 1);
        }
        distributionChartRepository.save(chart);
    }

    private DistributionChart findOrCreateDistributionChart(String chartType, String itemLabel) {
        return distributionChartRepository.findByChartTypeAndItemLabel(chartType, itemLabel)
                .orElse(null);
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