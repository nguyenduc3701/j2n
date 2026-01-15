package com.example.j2n.image_srv.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.j2n.image_srv.dto.InternalUserAuthentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class InternalAuthFilter extends OncePerRequestFilter {

    private static final String X_INTERNAL_TOKEN = "X-Internal-Token";
    private static final String X_USER_ID = "X-User-Id";
    private static final String X_USER_NAME = "X-User-Name";
    private static final String X_ROLE_ID = "X-Role-Id";

    @Value("${internal.token}")
    private String internalToken;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = request.getHeader(X_INTERNAL_TOKEN);
        String userId = request.getHeader(X_USER_ID);
        String userName = request.getHeader(X_USER_NAME);
        String roleId = request.getHeader(X_ROLE_ID);

        if (!internalToken.equals(token)) {
            log.error("[IMAGE-SRV] Invalid internal token");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Forbidden");
            return;
        }

        if (userId == null || userId.isBlank()) {
            log.error("[IMAGE-SRV] User not authenticated");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("User not authenticated");
            return;
        }

        InternalUserAuthentication authentication = new InternalUserAuthentication(userId, userName, roleId);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        filterChain.doFilter(request, response);
    }
}
