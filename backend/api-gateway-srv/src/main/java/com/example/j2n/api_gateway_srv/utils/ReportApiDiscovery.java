package com.example.j2n.api_gateway_srv.utils;

import com.example.j2n.lib.proto.ApiCatalogResponse;
import com.example.j2n.lib.proto.ReportServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReportApiDiscovery {

    private final GrpcFactory grpcFactory;

    @Value("${api-gateway.report.base-url}")
    private String reportBaseUrl;

    private final Map<String, String> reportApiMappings = new ConcurrentHashMap<>();
    private final AtomicBoolean isLoaded = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        log.info("[REPORT-DISCOVERY] Initializing API Discovery...");
        refreshCatalog();
    }

    @Scheduled(fixedDelayString = "${api-gateway.report.discovery.retry-ms:60000}")
    public void scheduledRefresh() {
        if (!isLoaded.get()) {
            log.info("[REPORT-DISCOVERY] Retrying API Catalog fetch...");
            refreshCatalog();
        }
    }

    public synchronized void refreshCatalog() {
        log.info("[REPORT-DISCOVERY] Fetching API Catalog from report-srv at {}", reportBaseUrl);
        ManagedChannel channel = null;
        try {
            channel = grpcFactory.createManagedChannel(reportBaseUrl);

            ReportServiceGrpc.ReportServiceBlockingStub stub = ReportServiceGrpc.newBlockingStub(channel);
            // Gọi gRPC lấy danh sách mapping
            ApiCatalogResponse response = stub.getApiCatalog(Empty.newBuilder().build());

            if (response != null && response.getMappingsCount() > 0) {
                // Lưu vào cache
                reportApiMappings.clear();
                reportApiMappings.putAll(response.getMappingsMap());
                isLoaded.set(true);

                log.info("[REPORT-DISCOVERY] Successfully loaded {} API mappings", reportApiMappings.size());
            }
        } catch (Exception e) {
            log.warn("[REPORT-DISCOVERY] Failed to load API mappings. Server might not be ready. Error: {}",
                    e.getMessage());
        } finally {
            if (channel != null) {
                channel.shutdown();
            }
        }
    }

    public String getMethodName(String path) {
        if (reportApiMappings.isEmpty()) {
            refreshCatalog();
        }
        return reportApiMappings.get(path);
    }

    public Map<String, String> getAllMappings() {
        return Collections.unmodifiableMap(reportApiMappings);
    }
}
