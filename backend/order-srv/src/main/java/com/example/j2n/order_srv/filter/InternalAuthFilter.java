package com.example.j2n.order_srv.filter;

import com.example.j2n.constants.CommonConst;
import com.example.j2n.security.InternalUserAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class InternalAuthFilter extends OncePerRequestFilter {

    private static final List<String> NOT_FILTER_LIST = List.of("/swagger-ui", "/v3/api-docs");

    @Value("${internal.token}")
    private String internalToken;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = request.getHeader(CommonConst.X_INTERNAL_TOKEN);
        String userId = request.getHeader(CommonConst.X_USER_ID);
        String userName = request.getHeader(CommonConst.X_USER_NAME);
        String roleId = request.getHeader(CommonConst.X_ROLE_ID);

        // Allow requests without internal token if they are in the NOT_FILTER_LIST
        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!internalToken.equals(token)) {
            log.error("[ORDER-SRV] Invalid internal token");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Forbidden - Invalid Internal Token");
            return;
        }

        if (userId == null || userId.isBlank()) {
            log.error("[ORDER-SRV] User not authenticated");
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

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return NOT_FILTER_LIST.stream().anyMatch(path::startsWith);
    }
}
