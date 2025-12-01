package com.example.j2n.bff_srv.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.j2n.bff_srv.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

@Component
public class JwtDecodeInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {
        log.info("[BFF-SRV] Pre-handle method called");
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                request.setAttribute("TOKEN", token);
                request.setAttribute("FROM-BFF", true);
            } catch (Exception e) {
                log.error("[BFF-SRV] Failed to decode JWT token", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
        log.info("[BFF-SRV] Pre-handle method completed");
        return true;
    }
}
