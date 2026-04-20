package com.example.j2n.bff_srv.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.service.response.LoginResponse;
import com.example.j2n.bff_srv.utils.RestClientUtil;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.RetryableException;
import com.example.j2n.utils.ResponseFactory;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AuthServiceClientTest {

    @Mock
    private RestClientUtil restClientUtil;

    @InjectMocks
    private AuthServiceClient authServiceClient;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        ServletRequestAttributes attributes = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void refresh_Success() {
        // Arrange
        Cookie refreshCookie = new Cookie("refresh_token", "old-refresh-token");
        when(request.getCookies()).thenReturn(new Cookie[] { refreshCookie });

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken("new-access-token");
        loginResponse.setRefreshToken("new-refresh-token");
        BaseResponse<LoginResponse> serviceResponse = ResponseFactory.success(loginResponse);

        when(restClientUtil.requestAuth(eq(GatewayPath.AUTH_REFRESH_TOKEN_PATH), eq(HttpMethod.POST), isNull(),
                anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(serviceResponse);

        // Act
        String result = authServiceClient.refresh();

        // Assert
        assertEquals("new-access-token", result);
        verify(response).addHeader(eq("X-New-Access-Token"), eq("new-access-token"));
        verify(response, atLeastOnce()).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
    }

    @Test
    void refresh_NoRefreshToken_ReturnsNull() {
        // Arrange
        when(request.getCookies()).thenReturn(null);

        // Act
        String result = authServiceClient.refresh();

        // Assert
        assertNull(result);
        verifyNoInteractions(restClientUtil);
    }

    @Test
    void refresh_Error_ThrowsRetryableException() {
        // Arrange
        Cookie refreshCookie = new Cookie("refresh_token", "old-refresh-token");
        when(request.getCookies()).thenReturn(new Cookie[] { refreshCookie });

        when(restClientUtil.requestAuth(anyString(), any(HttpMethod.class), any(), anyMap(),
                any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("API Error"));

        // Act & Assert
        assertThrows(RetryableException.class, () -> authServiceClient.refresh());
    }

    @Test
    void getTokenFromCookie_Success() {
        // Arrange
        Cookie cookie = new Cookie("test-cookie", "test-value");
        when(request.getCookies()).thenReturn(new Cookie[] { cookie });

        // Act
        String result = authServiceClient.getTokenFromCookie("test-cookie");

        // Assert
        assertEquals("test-value", result);
    }

    @Test
    void getTokenFromCookie_NotFound() {
        // Arrange
        Cookie cookie = new Cookie("other-cookie", "value");
        when(request.getCookies()).thenReturn(new Cookie[] { cookie });

        // Act
        String result = authServiceClient.getTokenFromCookie("test-cookie");

        // Assert
        assertNull(result);
    }

    @Test
    void getTokenFromCookie_NoCookies() {
        // Arrange
        when(request.getCookies()).thenReturn(null);

        // Act
        String result = authServiceClient.getTokenFromCookie("test-cookie");

        // Assert
        assertNull(result);
    }

    @Test
    void getTokenFromCookie_NoAttributes() {
        // Arrange
        RequestContextHolder.resetRequestAttributes();

        // Act
        String result = authServiceClient.getTokenFromCookie("test-cookie");

        // Assert
        assertNull(result);
    }

    @Test
    void refresh_ResponseNull_ReturnsNull() {
        // Arrange
        Cookie refreshCookie = new Cookie("refresh_token", "old-refresh-token");
        when(request.getCookies()).thenReturn(new Cookie[] { refreshCookie });

        when(restClientUtil.requestAuth(anyString(), any(HttpMethod.class), any(), anyMap(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(null);

        // Act
        String result = authServiceClient.refresh();

        // Assert
        assertNull(result);
    }

    @Test
    void refresh_DataNull_ReturnsNull() {
        // Arrange
        Cookie refreshCookie = new Cookie("refresh_token", "old-refresh-token");
        when(request.getCookies()).thenReturn(new Cookie[] { refreshCookie });

        BaseResponse<LoginResponse> serviceResponse = new BaseResponse<>();
        serviceResponse.setData(null);

        when(restClientUtil.requestAuth(anyString(), any(HttpMethod.class), any(), anyMap(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(serviceResponse);

        // Act
        String result = authServiceClient.refresh();

        // Assert
        assertNull(result);
    }

    @Test
    void updateResponseCredentials_Success() {
        // Act
        authServiceClient.updateResponseCredentials("new-access", "new-refresh");

        // Assert
        verify(response).addHeader(eq("X-New-Access-Token"), eq("new-access"));
        verify(response, times(2)).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
    }

    @Test
    void updateResponseCredentials_AttrNotNull_ResponseNull() {
        // Arrange
        ServletRequestAttributes attributes = new ServletRequestAttributes(request, null);
        RequestContextHolder.setRequestAttributes(attributes);

        // Act & Assert (Should not throw exception)
        authServiceClient.updateResponseCredentials("access", "refresh");
        verifyNoInteractions(request); // Just checking nothing else happens
    }

    @Test
    void updateResponseCredentials_NoResponseContext() {
        // Arrange
        RequestContextHolder.resetRequestAttributes();

        // Act & Assert (Should not throw exception)
        authServiceClient.updateResponseCredentials("access", "refresh");
    }
}
