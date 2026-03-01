package com.example.j2n.api_gateway_srv.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GrpcMetadataPropagationFilter implements GlobalFilter, Ordered {

    private static final String X_USER_ID = "X-User-Id";
    private static final String X_USER_NAME = "X-User-Name";
    private static final String X_ROLE_ID = "X-Role-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. Lấy thông tin từ exchange attributes (được set bởi JwtAuthFilter)
        String userId = exchange.getAttribute(X_USER_ID);
        String userName = exchange.getAttribute(X_USER_NAME);
        String roleId = exchange.getAttribute(X_ROLE_ID);

        // 2. Fallback: Nếu null thì thử lấy trực tiếp từ Request Headers (JwtAuthFilter
        // cũng đẩy vào đây)
        if (userId == null)
            userId = exchange.getRequest().getHeaders().getFirst(X_USER_ID);
        if (userName == null)
            userName = exchange.getRequest().getHeaders().getFirst(X_USER_NAME);
        if (roleId == null)
            roleId = exchange.getRequest().getHeaders().getFirst(X_ROLE_ID);

        log.debug("[GRPC-GATEWAY] Propagating metadata: userId={}, userName={}, role={}", userId, userName, roleId);

        if (userId == null && userName == null && roleId == null) {
            return chain.filter(exchange);
        }

        // Mutate request headers for gRPC transcoder
        // Ensure keys are lowercase, transcoder pushes these to gRPC metadata
        var requestBuilder = exchange.getRequest().mutate();

        if (userId != null)
            requestBuilder.header(X_USER_ID.toLowerCase(), userId);
        if (userName != null)
            requestBuilder.header(X_USER_NAME.toLowerCase(), userName);
        if (roleId != null)
            requestBuilder.header(X_ROLE_ID.toLowerCase(), roleId);

        return chain.filter(exchange.mutate().request(requestBuilder.build()).build());
    }

    @Override
    public int getOrder() {
        // Phải chạy TRƯỚC JsonToGrpc filter (đang ở order -2)
        return -10;
    }
}
