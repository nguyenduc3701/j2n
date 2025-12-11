package com.example.j2n.bff_srv.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

@Component
@Slf4j
public class JwtDecodeInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {
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
        return true;
    }
}
