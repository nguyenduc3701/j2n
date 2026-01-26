package com.example.j2n.bff_srv.interceptor;

import com.example.j2n.bff_srv.client.AuthServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestTemplateRefreshInterceptorTest {

    @Mock
    private ObjectProvider<AuthServiceClient> authServiceClientProvider;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private RestTemplateRefreshInterceptor interceptor;

    @Mock
    private HttpRequest request;

    @Mock
    private ClientHttpRequestExecution execution;

    @Mock
    private ClientHttpResponse response;

    @BeforeEach
    void setUp() {
        // No global stubbing to avoid UnnecessaryStubbingException
    }

    @Test
    void intercept_Success() throws IOException {
        // Arrange
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/test"));
        when(execution.execute(any(), any())).thenReturn(response);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);

        // Act
        ClientHttpResponse result = interceptor.intercept(request, new byte[0], execution);

        // Assert
        assertEquals(response, result);
        verify(execution, times(1)).execute(request, new byte[0]);
    }

    @Test
    void intercept_401_RefreshSuccess() throws IOException {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer expired-token");
        when(request.getHeaders()).thenReturn(headers);
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/test"));

        ClientHttpResponse response401 = mock(ClientHttpResponse.class);
        when(response401.getStatusCode()).thenReturn(HttpStatus.UNAUTHORIZED);
        when(execution.execute(any(), any())).thenReturn(response401).thenReturn(response);

        when(authServiceClientProvider.getObject()).thenReturn(authServiceClient);
        when(authServiceClient.getTokenFromCookie("access_token")).thenReturn("expired-token");
        when(authServiceClient.refresh()).thenReturn("new-token");

        // Act
        ClientHttpResponse result = interceptor.intercept(request, new byte[0], execution);

        // Assert
        assertEquals(response, result);
        assertEquals("Bearer new-token", headers.getFirst(HttpHeaders.AUTHORIZATION));
        verify(execution, times(2)).execute(eq(request), any());
    }

    @Test
    void intercept_401_RefreshByOtherThread() throws IOException {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer expired-token");
        when(request.getHeaders()).thenReturn(headers);
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/test"));

        ClientHttpResponse response401 = mock(ClientHttpResponse.class);
        when(response401.getStatusCode()).thenReturn(HttpStatus.UNAUTHORIZED);
        when(execution.execute(any(), any())).thenReturn(response401).thenReturn(response);

        when(authServiceClientProvider.getObject()).thenReturn(authServiceClient);
        when(authServiceClient.getTokenFromCookie("access_token")).thenReturn("refreshed-by-other-thread");

        // Act
        ClientHttpResponse result = interceptor.intercept(request, new byte[0], execution);

        // Assert
        assertEquals(response, result);
        assertEquals("Bearer refreshed-by-other-thread", headers.getFirst(HttpHeaders.AUTHORIZATION));
        verify(authServiceClient, never()).refresh();
    }

    @Test
    void intercept_401_OnRefreshPath_ReturnOriginal() throws IOException {
        // Arrange
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/auth/refresh-token"));
        ClientHttpResponse response401 = mock(ClientHttpResponse.class);
        when(response401.getStatusCode()).thenReturn(HttpStatus.UNAUTHORIZED);
        when(execution.execute(any(), any())).thenReturn(response401);

        // Act
        ClientHttpResponse result = interceptor.intercept(request, new byte[0], execution);

        // Assert
        assertEquals(response401, result);
    }

    @Test
    void intercept_401_RefreshFailed_ReturnOriginal() throws IOException {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer expired-token");
        when(request.getHeaders()).thenReturn(headers);
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/test"));

        ClientHttpResponse response401 = mock(ClientHttpResponse.class);
        when(response401.getStatusCode()).thenReturn(HttpStatus.UNAUTHORIZED);
        when(execution.execute(any(), any())).thenReturn(response401);

        when(authServiceClientProvider.getObject()).thenReturn(authServiceClient);
        when(authServiceClient.getTokenFromCookie("access_token")).thenReturn("expired-token");
        when(authServiceClient.refresh()).thenReturn(null);

        // Act
        ClientHttpResponse result = interceptor.intercept(request, new byte[0], execution);

        // Assert
        assertEquals(response401, result);
    }

    @Test
    void intercept_NoAuthHeader() throws IOException {
        // Arrange
        when(request.getHeaders()).thenReturn(new HttpHeaders());
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/test"));

        ClientHttpResponse response401 = mock(ClientHttpResponse.class);
        when(response401.getStatusCode()).thenReturn(HttpStatus.UNAUTHORIZED);
        when(execution.execute(any(), any())).thenReturn(response401);

        when(authServiceClientProvider.getObject()).thenReturn(authServiceClient);
        when(authServiceClient.getTokenFromCookie("access_token")).thenReturn(null);
        when(authServiceClient.refresh()).thenReturn(null);

        // Act
        ClientHttpResponse result = interceptor.intercept(request, new byte[0], execution);

        // Assert
        assertEquals(response401, result);
    }

    @Test
    void intercept_InvalidAuthHeader() throws IOException {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Basic abc");
        when(request.getHeaders()).thenReturn(headers);
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/test"));

        ClientHttpResponse response401 = mock(ClientHttpResponse.class);
        when(response401.getStatusCode()).thenReturn(HttpStatus.UNAUTHORIZED);
        when(execution.execute(any(), any())).thenReturn(response401);

        when(authServiceClientProvider.getObject()).thenReturn(authServiceClient);
        when(authServiceClient.getTokenFromCookie("access_token")).thenReturn(null);
        when(authServiceClient.refresh()).thenReturn(null);

        // Act
        ClientHttpResponse result = interceptor.intercept(request, new byte[0], execution);

        // Assert
        assertEquals(response401, result);
    }

    @Test
    void intercept_NullRequest_ThrowsException() {
        assertThrows(NullPointerException.class, () -> interceptor.intercept(null, new byte[0], execution));
    }

    @Test
    void intercept_NullBody_ThrowsException() {
        assertThrows(NullPointerException.class, () -> interceptor.intercept(request, null, execution));
    }

    @Test
    void intercept_NullExecution_ThrowsException() {
        assertThrows(NullPointerException.class, () -> interceptor.intercept(request, new byte[0], null));
    }
}
