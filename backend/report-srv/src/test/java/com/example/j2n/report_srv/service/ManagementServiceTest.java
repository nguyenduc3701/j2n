package com.example.j2n.report_srv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.lib.proto.ApiCatalogResponse;
import com.example.j2n.lib.proto.BaseProtoResponse;
import com.example.j2n.lib.proto.GetDashboardRequest;
import com.example.j2n.report_srv.constant.MessageEnum;
import com.example.j2n.report_srv.interceptor.GrpcServerAuthInterceptor;
import com.example.j2n.report_srv.messaging.user.event.UserRegisteredEvent;
import com.example.j2n.report_srv.repository.ActivePromotionRepository;
import com.example.j2n.report_srv.repository.DistributionChartRepository;
import com.example.j2n.report_srv.repository.MonthlyFinancialsRepository;
import com.example.j2n.report_srv.repository.RoomUtilityReportRepository;
import com.example.j2n.report_srv.repository.SummaryMetricsRepository;
import com.example.j2n.report_srv.repository.entity.ActivePromotion;
import com.example.j2n.report_srv.repository.entity.DistributionChart;
import com.example.j2n.report_srv.repository.entity.MonthlyFinancials;
import com.example.j2n.report_srv.repository.entity.RoomUtilityReport;
import com.example.j2n.report_srv.repository.entity.SummaryMetrics;
import com.example.j2n.report_srv.service.response.DashboardReportResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Empty;

import io.grpc.Context;
import io.grpc.stub.StreamObserver;

@ExtendWith(MockitoExtension.class)
class ManagementServiceTest {

    @Mock
    private SummaryMetricsRepository summaryMetricsRepository;
    @Mock
    private DistributionChartRepository distributionChartRepository;
    @Mock
    private MonthlyFinancialsRepository monthlyFinancialsRepository;
    @Mock
    private RoomUtilityReportRepository roomUtilityReportRepository;
    @Mock
    private ActivePromotionRepository activePromotionRepository;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ManagementService managementService;

    private UserRegisteredEvent validEvent;

    @BeforeEach
    void setUp() {
        validEvent = new UserRegisteredEvent();
        validEvent.setUserId("user-id");
        validEvent.setRole("ROLE_USER");
        validEvent.setStatus("ACTIVE");
        ReflectionTestUtils.setField(managementService, "self", managementService);
    }

    @Test
    void testGetApiCatalog() {
        StreamObserver<ApiCatalogResponse> observer = mock(StreamObserver.class);
        managementService.getApiCatalog(Empty.getDefaultInstance(), observer);

        ArgumentCaptor<ApiCatalogResponse> captor = ArgumentCaptor.forClass(ApiCatalogResponse.class);
        verify(observer).onNext(captor.capture());
        verify(observer).onCompleted();

        ApiCatalogResponse response = captor.getValue();
        assertNotNull(response);
    }

    @Test
    void testGetDashboardReport() throws Exception {
        StreamObserver<BaseProtoResponse> observer = mock(StreamObserver.class);

        when(summaryMetricsRepository.findAll()).thenReturn(List.of(new SummaryMetrics()));
        when(distributionChartRepository.findAll()).thenReturn(List.of(new DistributionChart()));
        when(monthlyFinancialsRepository.findAll()).thenReturn(List.of(new MonthlyFinancials()));
        when(roomUtilityReportRepository.findAll()).thenReturn(List.of(new RoomUtilityReport()));
        when(activePromotionRepository.findAll()).thenReturn(List.of(new ActivePromotion()));

        Context ctx = Context.current().withValue(GrpcServerAuthInterceptor.USER_ID_CTX, "user-id-123");
        Context previous = ctx.attach();

        try {
            managementService.getDashboardReport(GetDashboardRequest.getDefaultInstance(), observer);
        } finally {
            ctx.detach(previous);
        }

        verify(observer).onNext(any(BaseProtoResponse.class));
        verify(observer).onCompleted();
    }

    @Test
    void testGetDashboardReportResponse_Success() {
        SummaryMetrics summaryMetrics = new SummaryMetrics();
        DistributionChart distributionChart = new DistributionChart();
        MonthlyFinancials monthlyFinancials = new MonthlyFinancials();
        RoomUtilityReport roomUtilityReport = new RoomUtilityReport();
        ActivePromotion activePromotion = new ActivePromotion();

        when(summaryMetricsRepository.findAll()).thenReturn(List.of(summaryMetrics));
        when(distributionChartRepository.findAll()).thenReturn(List.of(distributionChart));
        when(monthlyFinancialsRepository.findAll()).thenReturn(List.of(monthlyFinancials));
        when(roomUtilityReportRepository.findAll()).thenReturn(List.of(roomUtilityReport));
        when(activePromotionRepository.findAll()).thenReturn(List.of(activePromotion));

        BaseResponse<DashboardReportResponse> response = managementService.getDashboardReportResponse();

        assertNotNull(response);
        assertEquals(String.valueOf(BaseMessageEnum.SUCCESS.getHttpStatus().getCode()), response.getCode());
        DashboardReportResponse data = response.getData();
        assertEquals(1, data.getSummaryMetrics().size());
        assertEquals(1, data.getDistributionCharts().size());
        assertEquals(1, data.getMonthlyFinancials().size());
        assertEquals(1, data.getRoomUtilityReports().size());
        assertEquals(1, data.getActivePromotions().size());
    }

    @Test
    void testGetDashboardReportResponse_WithExceptions() {
        when(summaryMetricsRepository.findAll()).thenThrow(new RuntimeException("DB Error"));
        when(distributionChartRepository.findAll()).thenThrow(new RuntimeException("DB Error"));
        when(monthlyFinancialsRepository.findAll()).thenThrow(new RuntimeException("DB Error"));
        when(roomUtilityReportRepository.findAll()).thenThrow(new RuntimeException("DB Error"));
        when(activePromotionRepository.findAll()).thenThrow(new RuntimeException("DB Error"));

        BaseResponse<DashboardReportResponse> response = managementService.getDashboardReportResponse();

        assertNotNull(response);
        assertEquals(String.valueOf(BaseMessageEnum.SUCCESS.getHttpStatus().getCode()), response.getCode());
        DashboardReportResponse data = response.getData();
        assertTrue(data.getSummaryMetrics().isEmpty());
        assertTrue(data.getDistributionCharts().isEmpty());
        assertTrue(data.getMonthlyFinancials().isEmpty());
        assertTrue(data.getRoomUtilityReports().isEmpty());
        assertTrue(data.getActivePromotions().isEmpty());
    }

    @Test
    void testHandleUserRegistrationReport_CreateNewRecords() {
        when(summaryMetricsRepository.findById(anyString())).thenReturn(Optional.empty());
        when(distributionChartRepository.findByChartTypeAndItemLabel("USER_TYPE", "ROLE_USER"))
                .thenReturn(Optional.empty());

        BaseResponse<Object> response = managementService.handleUserRegistrationReport(validEvent);

        assertNotNull(response);
        assertEquals(String.valueOf(MessageEnum.UPDATE_USER_REPORT_SUCCESS.getHttpStatus().getCode()),
                response.getCode());
        verify(summaryMetricsRepository, times(1)).save(any(SummaryMetrics.class));
        verify(distributionChartRepository).save(any(DistributionChart.class));
    }

    @Test
    void testHandleUserRegistrationReport_UpdateExistingRecords() {
        SummaryMetrics existingSummary = new SummaryMetrics();
        existingSummary.setMetricValue(5L);

        DistributionChart existingChart = new DistributionChart();
        existingChart.setItemValue(2L);

        when(summaryMetricsRepository.findById(anyString())).thenReturn(Optional.of(existingSummary));
        when(distributionChartRepository.findByChartTypeAndItemLabel("USER_TYPE", "ROLE_USER"))
                .thenReturn(Optional.of(existingChart));

        BaseResponse<Object> response = managementService.handleUserRegistrationReport(validEvent);

        assertNotNull(response);
        verify(summaryMetricsRepository, times(1)).save(existingSummary);
        assertEquals(6L, existingSummary.getMetricValue());

        verify(distributionChartRepository).save(existingChart);
        assertEquals(3L, existingChart.getItemValue());
    }

    @Test
    void testHandleUserRegistrationReport_ThrowsException() {
        when(summaryMetricsRepository.findById(anyString())).thenThrow(new RuntimeException("Test Exception"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            managementService.handleUserRegistrationReport(validEvent);
        });

        assertEquals("Test Exception", ex.getMessage());
        verify(distributionChartRepository, never()).save(any());
    }

    @Test
    void testHandleUserRegistrationReport_InactiveStatus() {
        validEvent.setStatus("INACTIVE");
        when(summaryMetricsRepository.findById(anyString())).thenReturn(Optional.empty());
        when(distributionChartRepository.findByChartTypeAndItemLabel(anyString(), anyString()))
                .thenReturn(Optional.empty());

        BaseResponse<Object> response = managementService.handleUserRegistrationReport(validEvent);

        assertNotNull(response);
        verify(summaryMetricsRepository, times(1)).save(any(SummaryMetrics.class));
    }

    @Test
    void testHandleProductReport_Increment() {
        SummaryMetrics summary = new SummaryMetrics();
        summary.setMetricValue(10L);
        when(summaryMetricsRepository.findById("total_products")).thenReturn(Optional.of(summary));

        BaseResponse<Object> response = managementService.handleProductReport(true);

        assertNotNull(response);
        assertEquals(11L, summary.getMetricValue());
        verify(summaryMetricsRepository).save(summary);
    }

    @Test
    void testHandleProductReport_Decrement() {
        SummaryMetrics summary = new SummaryMetrics();
        summary.setMetricValue(10L);
        when(summaryMetricsRepository.findById("total_products")).thenReturn(Optional.of(summary));

        BaseResponse<Object> response = managementService.handleProductReport(false);

        assertNotNull(response);
        assertEquals(9L, summary.getMetricValue());
        verify(summaryMetricsRepository).save(summary);
    }

    @Test
    void testHandleProductReport_DecrementToZero() {
        SummaryMetrics summary = new SummaryMetrics();
        summary.setMetricValue(0L);
        when(summaryMetricsRepository.findById("total_products")).thenReturn(Optional.of(summary));

        BaseResponse<Object> response = managementService.handleProductReport(false);

        assertNotNull(response);
        assertEquals(0L, summary.getMetricValue());
        verify(summaryMetricsRepository).save(summary);
    }

    @Test
    void testHandleProductReport_NewRecord_Decrement() {
        when(summaryMetricsRepository.findById("total_products")).thenReturn(Optional.empty());

        BaseResponse<Object> response = managementService.handleProductReport(false);

        assertNotNull(response);
        verify(summaryMetricsRepository).save(any(SummaryMetrics.class));

        ArgumentCaptor<SummaryMetrics> captor = ArgumentCaptor.forClass(SummaryMetrics.class);
        verify(summaryMetricsRepository).save(captor.capture());
        assertEquals(0L, captor.getValue().getMetricValue());
    }

    @Test
    void testHandleProductReport_NewRecord_Increment() {
        when(summaryMetricsRepository.findById("total_products")).thenReturn(Optional.empty());

        BaseResponse<Object> response = managementService.handleProductReport(true);

        assertNotNull(response);
        ArgumentCaptor<SummaryMetrics> captor = ArgumentCaptor.forClass(SummaryMetrics.class);
        verify(summaryMetricsRepository).save(captor.capture());
        assertEquals(1L, captor.getValue().getMetricValue());
    }

    @Test
    void testHandleProductReport_ThrowsException() {
        when(summaryMetricsRepository.findById(anyString())).thenThrow(new RuntimeException("DB Exception"));

        assertThrows(RuntimeException.class, () -> managementService.handleProductReport(true));
    }
}
