package com.example.j2n.auth_srv.filter;

import com.example.j2n.auth_srv.utils.JwtGeneralUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

import com.example.j2n.auth_srv.dto.JwtUserPrincipal;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtGeneralUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);
        try {
            Claims claims = jwtUtil.validate(token);
            String userId = claims.get("user_id", String.class);
            String username = claims.get("user_name", String.class);
            List<String> permissions = claims.get("permissions", List.class);
            JwtUserPrincipal principal = new JwtUserPrincipal(userId, username);
            List<SimpleGrantedAuthority> authorities = permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null,
                    authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            log.error("[AUTH-SRV] Invalid JWT token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
