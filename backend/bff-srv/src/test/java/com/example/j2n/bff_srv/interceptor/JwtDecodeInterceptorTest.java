package com.example.j2n.bff_srv.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtDecodeInterceptorTest {

    @InjectMocks
    private JwtDecodeInterceptor interceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Object handler;

    @Test
    void preHandle_Success() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer test-token");

        // Act
        boolean result = interceptor.preHandle(request, response, handler);

        // Assert
        assertTrue(result);
        verify(request).setAttribute("TOKEN", "test-token");
    }

    @Test
    void preHandle_NoHeader() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        boolean result = interceptor.preHandle(request, response, handler);

        // Assert
        assertTrue(result);
        verify(request, never()).setAttribute(anyString(), any());
    }

    @Test
    void preHandle_InvalidHeader() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader test-token");

        // Act
        boolean result = interceptor.preHandle(request, response, handler);

        // Assert
        assertTrue(result);
        verify(request, never()).setAttribute(anyString(), any());
    }

    @Test
    void preHandle_AttributeError_CallsSendError() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer test-token");
        doThrow(new RuntimeException("Test Exception")).when(request).setAttribute(eq("TOKEN"), eq("test-token"));

        // Act
        boolean result = interceptor.preHandle(request, response, handler);

        // Assert
        assertTrue(result);
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void preHandle_NullRequest_ThrowsException() {
        assertThrows(NullPointerException.class, () -> interceptor.preHandle(null, response, handler));
    }

    @Test
    void preHandle_NullResponse_ThrowsException() {
        assertThrows(NullPointerException.class, () -> interceptor.preHandle(request, null, handler));
    }

    @Test
    void preHandle_NullHandler_ThrowsException() {
        assertThrows(NullPointerException.class, () -> interceptor.preHandle(request, response, null));
    }
}
