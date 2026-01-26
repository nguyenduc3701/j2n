package com.example.j2n.auth_srv.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.j2n.auth_srv.utils.JwtGeneralUtil;
import io.jsonwebtoken.Claims;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtGeneralUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ShouldContinueChain_WhenHeaderIsMissing() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).validate(any());
    }

    @Test
    void doFilterInternal_ShouldContinueChain_WhenHeaderDoesNotStartWithBearer() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basic invalid");

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).validate(any());
    }

    @Test
    void doFilterInternal_ShouldAuthenticate_WhenTokenIsValid() throws ServletException, IOException {
        // Arrange
        String token = "validToken";
        String username = "testuser";
        String userId = "1";
        List<String> permissions = Collections.singletonList("USER");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        Claims claims = mock(Claims.class);
        when(jwtUtil.validate(token)).thenReturn(claims);
        when(claims.get("user_id", String.class)).thenReturn(userId);
        when(claims.get("user_name", String.class)).thenReturn(username);
        when(claims.get(eq("permissions"), eq(List.class))).thenReturn(permissions);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assert SecurityContextHolder.getContext().getAuthentication() != null;
        com.example.j2n.auth_srv.dto.JwtUserPrincipal principal = (com.example.j2n.auth_srv.dto.JwtUserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication().getPrincipal();
        assertEquals(username, principal.getUsername());
    }

    @Test
    void doFilterInternal_ShouldReturn401_WhenTokenIsInvalid() throws ServletException, IOException {
        // Arrange
        String token = "invalidToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validate(token)).thenThrow(new RuntimeException("Invalid token"));

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_ShouldReturn401_WhenPermissionsIsNull() throws ServletException, IOException {
        // Arrange
        String token = "validToken";
        String username = "testuser";
        String userId = "1";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        Claims claims = mock(Claims.class);
        when(jwtUtil.validate(token)).thenReturn(claims);
        when(claims.get("user_id", String.class)).thenReturn(userId);
        when(claims.get("user_name", String.class)).thenReturn(username);
        when(claims.get(eq("permissions"), eq(List.class))).thenReturn(null);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_ShouldHandleEmptyPermissions() throws ServletException, IOException {
        // Arrange
        String token = "validToken";
        String username = "testuser";
        String userId = "1";
        List<String> permissions = Collections.emptyList();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        Claims claims = mock(Claims.class);
        when(jwtUtil.validate(token)).thenReturn(claims);
        when(claims.get("user_id", String.class)).thenReturn(userId);
        when(claims.get("user_name", String.class)).thenReturn(username);
        when(claims.get(eq("permissions"), eq(List.class))).thenReturn(permissions);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assert SecurityContextHolder.getContext().getAuthentication() != null;
    }

    @Test
    void shouldNotFilter_ShouldReturnTrue_WhenPathStartsWithSwaggerUi() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/swagger-ui/index.html");

        // Act
        boolean result = jwtAuthFilter.shouldNotFilter(request);

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldNotFilter_ShouldReturnTrue_WhenPathStartsWithApiDocs() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/v3/api-docs");

        // Act
        boolean result = jwtAuthFilter.shouldNotFilter(request);

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldNotFilter_ShouldReturnFalse_WhenPathDoesNotMatch() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        // Act
        boolean result = jwtAuthFilter.shouldNotFilter(request);

        // Assert
        assertFalse(result);
    }
}
