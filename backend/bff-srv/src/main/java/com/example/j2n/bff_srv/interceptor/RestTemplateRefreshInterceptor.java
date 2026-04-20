package com.example.j2n.bff_srv.interceptor;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import com.example.j2n.bff_srv.client.AuthServiceClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class RestTemplateRefreshInterceptor implements ClientHttpRequestInterceptor {
    private static final String ACCESS_TOKEN = "access_token";
    private static final String BEARER = "Bearer ";
    private final ObjectProvider<AuthServiceClient> authServiceClientProvider;

    @Override
    @NonNull
    public ClientHttpResponse intercept(
            @NonNull HttpRequest request,
            @NonNull byte[] body,
            @NonNull ClientHttpRequestExecution execution) throws IOException {

        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        HttpServletRequest servletRequest = null;
        HttpServletResponse servletResponse = null;
        if (attrs instanceof ServletRequestAttributes sra) {
            servletRequest = sra.getRequest();
            servletResponse = sra.getResponse();
        }

        ClientHttpResponse response = execution.execute(request, body);
        String path = request.getURI().getPath();
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED && !path.contains("/auth/refresh-token")) {
            log.info("[BFF-SRV] Token expired, attempting silent refresh...");
            String expiredToken = getBearerToken(request);
            synchronized (this) {
                String currentToken = authServiceClientProvider.getObject().getTokenFromCookie(servletRequest,
                        ACCESS_TOKEN);
                if (currentToken != null && !currentToken.equals(expiredToken)) {
                    log.info("[BFF-SRV] Token was already refreshed by another thread, retrying...");
                    request.getHeaders().set(HttpHeaders.AUTHORIZATION, BEARER + currentToken);
                    return execution.execute(request, body);
                }
                String newAccessToken = authServiceClientProvider.getObject().refresh(servletRequest, servletResponse);
                if (newAccessToken != null) {
                    HttpHeaders headers = request.getHeaders();
                    headers.set(HttpHeaders.AUTHORIZATION, BEARER + newAccessToken);
                    log.info("[BFF-SRV] Refresh success, retrying original request");
                    return execution.execute(request, body);
                }
            }
        }

        return response;
    }

    private String getBearerToken(HttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(BEARER)) {
            return authHeader.substring(7);
        }
        return null;
    }
}
