package com.example.j2n.bff_srv.utils;

import com.example.j2n.bff_srv.config.GatewayConfig;
import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.dto.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RestClientUtilTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplate authRestTemplate;

    @Mock
    private GatewayConfig gatewayConfig;

    @Mock
    private ObjectMapper objectMapper;

    private RestClientUtil restClientUtil;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restClientUtil = new RestClientUtil(restTemplate, gatewayConfig, objectMapper, authRestTemplate);
        when(gatewayConfig.getBaseUrl()).thenReturn("http://localhost:8080");
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @SuppressWarnings("unchecked")
    void request_Success_WithToken() {
        when(request.getAttribute("TOKEN")).thenReturn("token");
        ResponseEntity<String> response = new ResponseEntity<>("ok", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);
        assertEquals("ok",
                restClientUtil.request("/test", HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
                }));
    }

    @Test
    @SuppressWarnings("unchecked")
    void request_Logout_Success() {
        when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("refresh_token", "rt") });
        ResponseEntity<String> response = new ResponseEntity<>("ok", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);
        assertEquals("ok", restClientUtil.request(GatewayPath.AUTH_LOGOUT_PATH, HttpMethod.POST, null,
                new ParameterizedTypeReference<String>() {
                }));
    }

    @Test
    @SuppressWarnings("unchecked")
    void request_Failed_Not2xx() {
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);
        assertThrows(RuntimeException.class,
                () -> restClientUtil.request("/test", HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
                }));
    }

    @Test
    @SuppressWarnings("unchecked")
    void requestUpload_Success() {
        when(request.getAttribute("TOKEN")).thenReturn("token");
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", "f");
        ResponseEntity<String> response = new ResponseEntity<>("ok", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);
        assertEquals("ok", restClientUtil.requestUpload("/upload", body, new ParameterizedTypeReference<String>() {
        }));
    }

    @Test
    @SuppressWarnings("unchecked")
    void requestUpload_NoToken_NoFiles_NullBody() {
        when(request.getAttribute("TOKEN")).thenReturn(null);
        // Cover body == null
        assertThrows(RuntimeException.class,
                () -> restClientUtil.requestUpload("/u", null, new ParameterizedTypeReference<String>() {
                }));
        // Cover !body.containsKey("files")
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        assertThrows(RuntimeException.class,
                () -> restClientUtil.requestUpload("/u", body, new ParameterizedTypeReference<String>() {
                }));
    }

    @Test
    @SuppressWarnings("unchecked")
    void requestUpload_Failed_Not2xx() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", "f");
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);
        assertThrows(RuntimeException.class,
                () -> restClientUtil.requestUpload("/u", body, new ParameterizedTypeReference<String>() {
                }));
    }

    @Test
    @SuppressWarnings("unchecked")
    void requestUpload_Exception() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", "f");
        when(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        assertThrows(RuntimeException.class,
                () -> restClientUtil.requestUpload("/u", body, new ParameterizedTypeReference<String>() {
                }));
    }

    @Test
    void requestBinary_Success() {
        when(request.getAttribute("TOKEN")).thenReturn("token");
        ResponseEntity<byte[]> response = new ResponseEntity<>("ok".getBytes(), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(byte[].class))).thenReturn(response);
        assertNotNull(restClientUtil.requestBinary("/b"));
    }

    @Test
    void requestBinary_NoToken_Failed() {
        when(request.getAttribute("TOKEN")).thenReturn(null);
        ResponseEntity<byte[]> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        when(restTemplate.exchange(anyString(), any(), any(), eq(byte[].class))).thenReturn(response);
        assertThrows(RuntimeException.class, () -> restClientUtil.requestBinary("/b"));
    }

    @Test
    void requestBinary_Exception() {
        when(restTemplate.exchange(anyString(), any(), any(), eq(byte[].class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));
        assertThrows(RuntimeException.class, () -> restClientUtil.requestBinary("/b"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void requestAuth_Cases() {
        ResponseEntity<String> response = new ResponseEntity<>("ok", HttpStatus.OK);
        when(authRestTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);
        // Custom headers
        restClientUtil.requestAuth("/a", HttpMethod.GET, "b", Map.of("k", "v"),
                new ParameterizedTypeReference<String>() {
                });
        // No custom headers
        restClientUtil.requestAuth("/a", HttpMethod.POST, null, null, new ParameterizedTypeReference<String>() {
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    void buildExternalException_JsonError() throws Exception {
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "", "invalid".getBytes(),
                null);
        when(objectMapper.readValue(anyString(), eq(BaseResponse.class)))
                .thenThrow(new com.fasterxml.jackson.core.JsonParseException(null, ""));
        when(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class))).thenThrow(ex);
        assertThrows(RuntimeException.class,
                () -> restClientUtil.request("/e", HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
                }));
    }

    @Test
    @SuppressWarnings("unchecked")
    void buildExternalException_Success() throws Exception {
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "", "{}".getBytes(), null);
        BaseResponse br = new BaseResponse();
        when(objectMapper.readValue(anyString(), eq(BaseResponse.class))).thenReturn(br);
        when(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class))).thenThrow(ex);
        assertThrows(RuntimeException.class,
                () -> restClientUtil.request("/e", HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
                }));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getCurrentToken_Edge() {
        ResponseEntity<String> response = new ResponseEntity<>("ok", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        // No attributes
        RequestContextHolder.resetRequestAttributes();
        restClientUtil.request("/t", HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
        });

        // Not Servlet attributes
        RequestContextHolder
                .setRequestAttributes(mock(org.springframework.web.context.request.RequestAttributes.class));
        restClientUtil.request("/t", HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
        });

        // Empty token
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(request.getAttribute("TOKEN")).thenReturn("");
        restClientUtil.request("/t", HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
        });
    }

    @Test
    void getRefreshTokenFromCookie_All() {
        // Attr null
        RequestContextHolder.resetRequestAttributes();
        assertNull(restClientUtil.getRefreshTokenFromCookie());

        // Cookies null
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(request.getCookies()).thenReturn(null);
        assertNull(restClientUtil.getRefreshTokenFromCookie());

        // Multiple cookies, ref token middle
        Cookie c1 = new Cookie("a", "b");
        Cookie c2 = new Cookie("refresh_token", "val");
        when(request.getCookies()).thenReturn(new Cookie[] { c1, c2 });
        assertEquals("val", restClientUtil.getRefreshTokenFromCookie());

        // Not found
        when(request.getCookies()).thenReturn(new Cookie[] { c1 });
        assertNull(restClientUtil.getRefreshTokenFromCookie());
    }

    @Test
    @SuppressWarnings("unchecked")
    void requestUpload_TokenEmpty() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(request.getAttribute("TOKEN")).thenReturn("");
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", "f");
        ResponseEntity<String> response = new ResponseEntity<>("ok", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);
        restClientUtil.requestUpload("/u", body, new ParameterizedTypeReference<String>() {
        });
        // This covers !token.isEmpty() false in requestUpload
    }

    @Test
    void requestBinary_TokenEmpty() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(request.getAttribute("TOKEN")).thenReturn("");
        ResponseEntity<byte[]> response = new ResponseEntity<>("ok".getBytes(), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(byte[].class))).thenReturn(response);
        restClientUtil.requestBinary("/b");
        // This covers !token.isEmpty() false in requestBinary
    }

    @Test
    @SuppressWarnings("unchecked")
    void requestAuth_EmptyHeaders() {
        ResponseEntity<String> response = new ResponseEntity<>("ok", HttpStatus.OK);
        when(authRestTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);
        restClientUtil.requestAuth("/a", HttpMethod.GET, "b", Map.of(), new ParameterizedTypeReference<String>() {
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    void request_Logout_NoCookie() {
        when(request.getCookies()).thenReturn(null);
        ResponseEntity<String> response = new ResponseEntity<>("ok", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);
        restClientUtil.request(GatewayPath.AUTH_LOGOUT_PATH, HttpMethod.POST, null,
                new ParameterizedTypeReference<String>() {
                });
    }
}
