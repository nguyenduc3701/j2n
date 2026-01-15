package com.example.j2n.bff_srv.utils;

import com.example.j2n.bff_srv.config.GatewayConfig;
import com.example.j2n.bff_srv.constant.MessageEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RestClientUtilTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private GatewayConfig gatewayConfig;

    @InjectMocks
    private RestClientUtil restClientUtil;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(gatewayConfig.getBaseUrl()).thenReturn("http://localhost:8080");

        // Mock RequestContextHolder
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void request_Success_WithToken() {
        when(request.getAttribute("TOKEN")).thenReturn("test-token");
        ResponseEntity<String> response = new ResponseEntity<>("success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);

        String result = restClientUtil.request("/test", HttpMethod.GET, null, String.class);

        assertEquals("success", result);
        verify(restTemplate).exchange(eq("http://localhost:8080/test"), eq(HttpMethod.GET), argThat(entity -> {
            assertEquals("Bearer test-token", entity.getHeaders().getFirst("Authorization"));
            assertEquals("true", entity.getHeaders().getFirst("FROM-BFF"));
            return true;
        }), eq(String.class));
    }

    @Test
    void request_Success_NoToken() {
        when(request.getAttribute("TOKEN")).thenReturn(null);
        ResponseEntity<String> response = new ResponseEntity<>("success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);

        String result = restClientUtil.request("/test", HttpMethod.GET, null, String.class);

        assertEquals("success", result);
        verify(restTemplate).exchange(anyString(), any(HttpMethod.class), argThat(entity -> {
            assertNull(entity.getHeaders().getFirst("Authorization"));
            return true;
        }), eq(String.class));
    }

    @Test
    void request_Failed_Not2xx() {
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> restClientUtil.request("/test", HttpMethod.GET, null, String.class));

        assertTrue(exception.getMessage().contains("to gateway failed"));
    }

    @Test
    void request_Failed_HttpStatusCodeException() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        assertThrows(RuntimeException.class, () -> restClientUtil.request("/test", HttpMethod.GET, null, String.class));
    }

    @Test
    void requestUpload_Success() {
        when(request.getAttribute("TOKEN")).thenReturn("test-token");
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", "dummy-resource");

        ResponseEntity<String> response = new ResponseEntity<>("success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);

        String result = restClientUtil.requestUpload("/upload", body, String.class);

        assertEquals("success", result);
        verify(restTemplate).exchange(eq("http://localhost:8080/upload"), eq(HttpMethod.POST), any(), eq(String.class));
    }

    @Test
    void requestUpload_NoFiles_ThrowsException() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        assertThrows(RuntimeException.class, () -> restClientUtil.requestUpload("/upload", body, String.class));
    }

    @Test
    void requestUpload_Failed_HttpStatusCodeException() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", "dummy-resource");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(Class.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(RuntimeException.class, () -> restClientUtil.requestUpload("/upload", body, String.class));
    }

    @Test
    void requestBinary_Success() {
        when(request.getAttribute("TOKEN")).thenReturn("test-token");
        byte[] content = "binary".getBytes();
        ResponseEntity<byte[]> response = new ResponseEntity<>(content, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(byte[].class)))
                .thenReturn(response);

        ResponseEntity<byte[]> result = restClientUtil.requestBinary("/binary");

        assertEquals(content, result.getBody());
    }

    @Test
    void request_NoRequestContext() {
        RequestContextHolder.resetRequestAttributes();
        ResponseEntity<String> response = new ResponseEntity<>("success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);

        String result = restClientUtil.request("/test", HttpMethod.GET, null, String.class);

        assertEquals("success", result);
        verify(restTemplate).exchange(anyString(), any(HttpMethod.class), argThat(entity -> {
            assertNull(entity.getHeaders().getFirst("Authorization"));
            return true;
        }), eq(String.class));
    }
}
