package com.example.j2n.bff_srv.interceptor;

import com.example.j2n.bff_srv.client.AuthServiceClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientRefreshInterceptor implements ExchangeFilterFunction {

    private static final String ACCESS_TOKEN = "access_token";
    private static final String BEARER = "Bearer ";
    private final AuthServiceClient authServiceClient;

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return Mono.deferContextual(ctx -> {
            // Lấy RequestAttributes từ Context của Reactor bằng class truyền vào từ .contextWrite()
            RequestAttributes attrs = ctx.getOrDefault(RequestAttributes.class, null);
            
            HttpServletRequest servletRequest = null;
            HttpServletResponse servletResponse = null;
            String token = null;

            if (attrs instanceof ServletRequestAttributes sra) {
                servletRequest = sra.getRequest();
                servletResponse = sra.getResponse();
                token = (String) servletRequest.getAttribute("TOKEN");
            }

            final HttpServletRequest finalServletRequest = servletRequest;
            final HttpServletResponse finalServletResponse = servletResponse;

            ClientRequest.Builder builder = ClientRequest.from(request)
                    .header("FROM-BFF", "true");
            if (token != null && !token.isEmpty()) {
                builder.header("Authorization", BEARER + token);
            }

            return next.exchange(builder.build()).flatMap(response -> {
                String path = request.url().getPath();
                if (response.statusCode() == HttpStatus.UNAUTHORIZED && !path.contains("/auth/refresh-token")) {
                    log.info("[BFF-SRV] GraphQL/WebClient Token expired, attempting silent refresh...");
                    String expiredToken = getBearerToken(request);
                    
                    synchronized (this) {
                        if (finalServletRequest == null) {
                            log.error("[BFF-SRV] Cannot refresh token: Missing HttpServletRequest context from Reactor Context.");
                            return Mono.just(response);
                        }

                        // Kiểm tra xem token trong cookie đã được thread khác refresh chưa
                        String currentTokenInCookie = authServiceClient.getTokenFromCookie(finalServletRequest, ACCESS_TOKEN);
                        if (currentTokenInCookie != null && !currentTokenInCookie.equals(expiredToken)) {
                            log.info("[BFF-SRV] Token was already refreshed by another thread, retrying current WebClient request...");
                            return retryRequest(request, next, currentTokenInCookie);
                        }

                        // Thực sự refresh nếu chưa có thread nào làm
                        String newToken = authServiceClient.refresh(finalServletRequest, finalServletResponse);
                        if (newToken != null) {
                            log.info("[BFF-SRV] Refresh success, retrying original GraphQL/WebClient request");
                            return retryRequest(request, next, newToken);
                        }
                    }
                }
                return Mono.just(response);
            });
        });
    }

    private Mono<ClientResponse> retryRequest(ClientRequest request, ExchangeFunction next, String token) {
        ClientRequest retryRequest = ClientRequest.from(request)
                .header("Authorization", BEARER + token)
                .header("FROM-BFF", "true")
                .build();
        return next.exchange(retryRequest);
    }

    private String getBearerToken(ClientRequest request) {
        String authHeader = request.headers().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith(BEARER)) {
            return authHeader.substring(7);
        }
        return null;
    }
}
