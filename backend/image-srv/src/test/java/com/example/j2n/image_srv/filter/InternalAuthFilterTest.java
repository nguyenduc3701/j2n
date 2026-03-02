package com.example.j2n.image_srv.filter;

import com.example.j2n.constants.CommonConst;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

class InternalAuthFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private InternalAuthFilter internalAuthFilter;

    private static final String INTERNAL_TOKEN = "test-token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(internalAuthFilter, "internalToken", INTERNAL_TOKEN);
    }

    @Test
    void doFilterInternal_Success() throws ServletException, IOException {
        when(request.getHeader(CommonConst.X_INTERNAL_TOKEN)).thenReturn(INTERNAL_TOKEN);
        when(request.getHeader(CommonConst.X_USER_ID)).thenReturn("user123");
        when(request.getHeader(CommonConst.X_USER_NAME)).thenReturn("testuser");
        when(request.getHeader(CommonConst.X_ROLE_ID)).thenReturn("role1");

        internalAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidToken_ReturnsForbidden() throws ServletException, IOException {
        when(request.getHeader(CommonConst.X_INTERNAL_TOKEN)).thenReturn("wrong-token");
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        internalAuthFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_NullUserId_ReturnsUnauthorized() throws ServletException, IOException {
        when(request.getHeader(CommonConst.X_INTERNAL_TOKEN)).thenReturn(INTERNAL_TOKEN);
        when(request.getHeader(CommonConst.X_USER_ID)).thenReturn(null);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        internalAuthFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_BlankUserId_ReturnsUnauthorized() throws ServletException, IOException {
        when(request.getHeader(CommonConst.X_INTERNAL_TOKEN)).thenReturn(INTERNAL_TOKEN);
        when(request.getHeader(CommonConst.X_USER_ID)).thenReturn("");
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        internalAuthFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
    }
}
