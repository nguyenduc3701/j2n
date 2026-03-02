package com.example.j2n.api_gateway_srv.service;

import com.example.j2n.api_gateway_srv.utils.GrpcFactory;
import com.example.j2n.constants.CommonConst;
import com.example.j2n.lib.proto.ReportServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DynamicGrpcInvoker {
    private static final Metadata.Key<String> KEY_INTERNAL_TOKEN = Metadata.Key.of(CommonConst.X_INTERNAL_TOKEN,
            Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> KEY_USER_ID = Metadata.Key.of(CommonConst.X_USER_ID,
            Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> KEY_USER_NAME = Metadata.Key.of(CommonConst.X_USER_NAME,
            Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> KEY_ROLE_ID = Metadata.Key.of(CommonConst.X_ROLE_ID,
            Metadata.ASCII_STRING_MARSHALLER);

    @Value("${api-gateway.report.base-url}")
    private String reportBaseUrl;

    @Value("${internal.token}")
    private String internalToken;

    private final GrpcFactory grpcFactory;
    private ManagedChannel channel;
    private ReportServiceGrpc.ReportServiceBlockingStub blockingStub;

    @PostConstruct
    public void init() {
        try {
            this.channel = grpcFactory.createManagedChannel(reportBaseUrl);
            this.blockingStub = ReportServiceGrpc.newBlockingStub(channel);
        } catch (Exception e) {
            log.error("[DYNAMIC-GRPC] Failed to initialize gRPC channel for: {}", reportBaseUrl, e);
        }
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null) {
            channel.shutdown();
        }
    }

    public String invoke(String methodName, String jsonBody,
            String userId, String userName, String roleId) throws Exception {

        // 1. Chuẩn bị Metadata (Headers cho gRPC)
        Metadata metadata = createMetadata(userId, userName, roleId);

        // 2. Gắn metadata vào Stub bằng Interceptor
        ReportServiceGrpc.ReportServiceBlockingStub stubWithHeaders = blockingStub
                .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(metadata));

        // 3. Thực hiện gọi động
        return grpcFactory.dynamicInvoke(stubWithHeaders, methodName, jsonBody);
    }

    private Metadata createMetadata(String userId, String userName, String roleId) {
        Metadata metadata = new Metadata();
        putIfNotNull(metadata, KEY_INTERNAL_TOKEN, internalToken);
        putIfNotNull(metadata, KEY_USER_ID, userId);
        putIfNotNull(metadata, KEY_USER_NAME, userName);
        putIfNotNull(metadata, KEY_ROLE_ID, roleId);
        return metadata;
    }

    private void putIfNotNull(Metadata metadata, Metadata.Key<String> key, String value) {
        if (value != null) {
            metadata.put(key, value);
        }
    }
}
