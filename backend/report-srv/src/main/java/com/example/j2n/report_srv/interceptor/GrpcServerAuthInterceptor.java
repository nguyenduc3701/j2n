package com.example.j2n.report_srv.interceptor;

import com.example.j2n.constants.CommonConst;
import com.example.j2n.security.InternalUserAuthentication;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@Component
public class GrpcServerAuthInterceptor implements ServerInterceptor {

    private static final Metadata.Key<String> X_INTERNAL_TOKEN = Metadata.Key.of(CommonConst.X_INTERNAL_TOKEN,
            Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> X_USER_ID = Metadata.Key.of(CommonConst.X_USER_ID,
            Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> X_USER_NAME = Metadata.Key.of(CommonConst.X_USER_NAME,
            Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> X_ROLE_ID = Metadata.Key.of(CommonConst.X_ROLE_ID,
            Metadata.ASCII_STRING_MARSHALLER);

    // Context Keys for internal gRPC code usage if needed
    public static final Context.Key<String> USER_ID_CTX = Context.key("userId");

    @Value("${internal.token}")
    private String internalToken;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String fullMethodName = call.getMethodDescriptor().getFullMethodName();
        log.debug("[GRPC-AUTH] Intercepting: {}", fullMethodName);

        // Bypass check for discovery method
        if (fullMethodName.endsWith("GetApiCatalog")) {
            return next.startCall(call, headers);
        }

        String token = headers.get(X_INTERNAL_TOKEN);
        String userId = headers.get(X_USER_ID);
        String userName = headers.get(X_USER_NAME);
        String roleId = headers.get(X_ROLE_ID);

        log.debug("[GRPC-AUTH] Extracted headers: userId={}, tokenPresent={}", userId, token != null);

        // 1. Validate Internal Token
        if (internalToken == null || !internalToken.equals(token)) {
            log.error("[GRPC-AUTH] Invalid internal token in metadata");
            call.close(Status.PERMISSION_DENIED.withDescription("Invalid internal token"), new Metadata());
            return new ServerCall.Listener<ReqT>() {
            };
        }

        // 2. Validate User Identification
        if (userId == null || userId.isBlank()) {
            log.error("[GRPC-AUTH] Missing user ID in metadata");
            call.close(Status.UNAUTHENTICATED.withDescription("User mapping failed"), new Metadata());
            return new ServerCall.Listener<ReqT>() {
            };
        }

        // 3. Set Spring Security Context (for components that rely on
        // SecurityContextHolder)
        InternalUserAuthentication authentication = new InternalUserAuthentication(userId, userName, roleId);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // 4. Wrap for gRPC Context (optional but recommended for async boundary safety)
        Context grpcContext = Context.current().withValue(USER_ID_CTX, userId);

        return Contexts.interceptCall(grpcContext, call, headers, next);
    }
}
