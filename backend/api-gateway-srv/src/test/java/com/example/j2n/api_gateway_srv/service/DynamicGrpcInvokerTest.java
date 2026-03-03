package com.example.j2n.api_gateway_srv.service;

import com.example.j2n.api_gateway_srv.utils.GrpcFactory;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DynamicGrpcInvokerTest {

    @Mock
    private GrpcFactory grpcFactory;

    @Mock
    private ManagedChannel channel;

    private DynamicGrpcInvoker invoker;

    @BeforeEach
    void setUp() throws Exception {
        invoker = new DynamicGrpcInvoker(grpcFactory);
        ReflectionTestUtils.setField(invoker, "reportBaseUrl", "http://localhost:18184");
        ReflectionTestUtils.setField(invoker, "internalToken", "test-token");

        when(grpcFactory.createManagedChannel(anyString())).thenReturn(channel);
        invoker.init();
    }

    @Test
    void init_Success() throws Exception {
        verify(grpcFactory).createManagedChannel("http://localhost:18184");
    }

    @Test
    void init_Exception() throws Exception {
        DynamicGrpcInvoker errorInvoker = new DynamicGrpcInvoker(grpcFactory);
        ReflectionTestUtils.setField(errorInvoker, "reportBaseUrl", "invalid");
        when(grpcFactory.createManagedChannel(anyString())).thenThrow(new RuntimeException("error"));

        // Nên log lỗi và không crash
        errorInvoker.init();
    }

    @Test
    void invoke_Success() throws Exception {
        when(grpcFactory.dynamicInvoke(any(), anyString(), anyString())).thenReturn("{\"status\":\"ok\"}");

        String result = invoker.invoke("GetDashboardReport", "{}", "user1", "test", "admin");

        assertEquals("{\"status\":\"ok\"}", result);
        verify(grpcFactory).dynamicInvoke(any(), eq("GetDashboardReport"), eq("{}"));
    }

    @Test
    void shutdown_Success() {
        invoker.shutdown();
        verify(channel).shutdown();
    }

    @Test
    void shutdown_NullChannel() {
        DynamicGrpcInvoker nullInvoker = new DynamicGrpcInvoker(grpcFactory);
        nullInvoker.shutdown(); // Không được throw NullPointerException
    }

    @Test
    void invoke_Exception() throws Exception {
        when(grpcFactory.dynamicInvoke(any(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Invoke failed"));

        assertThrows(RuntimeException.class,
                () -> invoker.invoke("GetDashboardReport", "{}", "user1", "test", "admin"));
    }
}
