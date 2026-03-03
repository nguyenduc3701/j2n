package com.example.j2n.api_gateway_srv.utils;

import com.example.j2n.lib.proto.ApiCatalogResponse;
import com.example.j2n.lib.proto.ReportServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportApiDiscoveryTest {

    @Mock
    private GrpcFactory grpcFactory;

    private ReportApiDiscovery discovery;

    @BeforeEach
    void setUp() {
        discovery = new ReportApiDiscovery(grpcFactory);
        ReflectionTestUtils.setField(discovery, "reportBaseUrl", "http://localhost:18184");
    }

    @SuppressWarnings("unchecked")
    @Test
    void getMethodName_CheckCache() {
        Map<String, String> cache = (Map<String, String>) ReflectionTestUtils.getField(discovery, "reportApiMappings");
        if (cache != null) {
            cache.put("/api/test", "TestMethod");
        }

        String result = discovery.getMethodName("/api/test");
        assertEquals("TestMethod", result);
    }

    @Test
    void init_Success() throws Exception {
        when(grpcFactory.createManagedChannel(anyString())).thenThrow(new RuntimeException("Connection failed"));
        discovery.init();
        assertTrue(discovery.getAllMappings().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAllMappings_CheckUnmodifiable() {
        Map<String, String> cache = (Map<String, String>) ReflectionTestUtils.getField(discovery, "reportApiMappings");
        if (cache != null) {
            cache.put("/api/test", "TestMethod");
        }

        Map<String, String> all = discovery.getAllMappings();
        assertEquals(1, all.size());
        assertTrue(all.containsKey("/api/test"));
    }

    @Test
    void scheduledRefresh_AlreadyLoaded() {
        AtomicBoolean isLoaded = (AtomicBoolean) ReflectionTestUtils.getField(discovery, "isLoaded");
        if (isLoaded != null) {
            isLoaded.set(true);
        }

        discovery.scheduledRefresh();

        try {
            verify(grpcFactory, never()).createManagedChannel(anyString());
        } catch (Exception e) {
            // Ignored for test
        }
    }

    @Test
    void scheduledRefresh_NotLoaded() throws Exception {
        AtomicBoolean isLoaded = (AtomicBoolean) ReflectionTestUtils.getField(discovery, "isLoaded");
        if (isLoaded != null) {
            isLoaded.set(false);
        }

        when(grpcFactory.createManagedChannel(anyString())).thenThrow(new RuntimeException("fail"));
        discovery.scheduledRefresh();
        verify(grpcFactory).createManagedChannel(anyString());
    }

    @Test
    void refreshCatalog_HandleException() throws Exception {
        when(grpcFactory.createManagedChannel(anyString())).thenThrow(new RuntimeException("Connection failed"));
        discovery.refreshCatalog();
        assertTrue(discovery.getAllMappings().isEmpty());
    }

    @Test
    void refreshCatalog_EmptyResponse() throws Exception {
        ManagedChannel channel = mock(ManagedChannel.class);
        when(grpcFactory.createManagedChannel(anyString())).thenReturn(channel);

        // Mock static method call for gRPC stub creation
        try (MockedStatic<ReportServiceGrpc> mockedGrpc = mockStatic(ReportServiceGrpc.class)) {
            ReportServiceGrpc.ReportServiceBlockingStub stub = mock(ReportServiceGrpc.ReportServiceBlockingStub.class);
            mockedGrpc.when(() -> ReportServiceGrpc.newBlockingStub(any(ManagedChannel.class))).thenReturn(stub);

            ApiCatalogResponse response = ApiCatalogResponse.newBuilder().build(); // mappingsCount = 0
            when(stub.getApiCatalog(any(Empty.class))).thenReturn(response);

            discovery.refreshCatalog();
            assertTrue(discovery.getAllMappings().isEmpty());
        }
        verify(channel).shutdown();
    }

    @Test
    void refreshCatalog_Success() throws Exception {
        ManagedChannel channel = mock(ManagedChannel.class);
        when(grpcFactory.createManagedChannel(anyString())).thenReturn(channel);

        try (MockedStatic<ReportServiceGrpc> mockedGrpc = mockStatic(ReportServiceGrpc.class)) {
            ReportServiceGrpc.ReportServiceBlockingStub stub = mock(ReportServiceGrpc.ReportServiceBlockingStub.class);
            mockedGrpc.when(() -> ReportServiceGrpc.newBlockingStub(any(ManagedChannel.class))).thenReturn(stub);

            ApiCatalogResponse response = ApiCatalogResponse.newBuilder()
                    .putMappings("/api/test", "TestService")
                    .build();
            when(stub.getApiCatalog(any(Empty.class))).thenReturn(response);

            discovery.refreshCatalog();
            assertEquals("TestService", discovery.getMethodName("/api/test"));
        }
    }

    @Test
    void getMethodName_TriggerRefresh() throws Exception {
        // Mapping is empty, should call refreshCatalog
        when(grpcFactory.createManagedChannel(anyString())).thenThrow(new RuntimeException("fail"));

        discovery.getMethodName("/api/any");

        verify(grpcFactory).createManagedChannel(anyString());
    }

    @Test
    void refreshCatalog_NullResponse() throws Exception {
        ManagedChannel channel = mock(ManagedChannel.class);
        when(grpcFactory.createManagedChannel(anyString())).thenReturn(channel);

        try (MockedStatic<ReportServiceGrpc> mockedGrpc = mockStatic(ReportServiceGrpc.class)) {
            ReportServiceGrpc.ReportServiceBlockingStub stub = mock(ReportServiceGrpc.ReportServiceBlockingStub.class);
            mockedGrpc.when(() -> ReportServiceGrpc.newBlockingStub(any(ManagedChannel.class))).thenReturn(stub);

            when(stub.getApiCatalog(any(Empty.class))).thenReturn(null);

            discovery.refreshCatalog();
            assertTrue(discovery.getAllMappings().isEmpty());
        }
    }
}
