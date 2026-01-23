package com.example.j2n.api_gateway_srv.filter;

import com.example.j2n.api_gateway_srv.constant.MessageEnum;
import com.example.j2n.api_gateway_srv.utils.JwtGeneralUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtGeneralUtil jwtGeneralUtil;

    @Mock
    private WebFilterChain filterChain;

    private JwtAuthFilter jwtAuthFilter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        jwtAuthFilter = new JwtAuthFilter(jwtGeneralUtil, objectMapper);
    }

    @Test
    void filter_ShouldBypass_WhenPathInWhiteList() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/auth/login").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        when(filterChain.filter(any())).thenReturn(Mono.empty());

        StepVerifier.create(jwtAuthFilter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
    }

    @Test
    void filter_ShouldReturnError_WhenNotFromBff() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/user/me")
                .header("FROM-BFF", "false")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(jwtAuthFilter.filter(exchange, filterChain))
                .verifyComplete();

        verifyNoInteractions(filterChain);
    }

    @Test
    void filter_ShouldReturnError_WhenAuthHeaderMissing() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/user/me")
                .header("FROM-BFF", "true")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(jwtAuthFilter.filter(exchange, filterChain))
                .verifyComplete();

        verifyNoInteractions(filterChain);
    }

    @Test
    void filter_ShouldReturnError_WhenTokenInvalid() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/user/me")
                .header("FROM-BFF", "true")
                .header("Authorization", "Bearer invalid-token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        when(jwtGeneralUtil.verify(anyString())).thenThrow(new RuntimeException("Invalid token"));

        StepVerifier.create(jwtAuthFilter.filter(exchange, filterChain))
                .verifyComplete();

        verifyNoInteractions(filterChain);
    }

    @Test
    void filter_ShouldProceed_WhenTokenValid() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/user/me")
                .header("FROM-BFF", "true")
                .header("Authorization", "Bearer valid-token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Claims claims = new DefaultClaims();
        claims.put("user_id", "1");
        claims.put("user_name", "testuser");
        claims.put("role_id", "admin");

        when(jwtGeneralUtil.verify("valid-token")).thenReturn(claims);
        when(jwtGeneralUtil.getInternalToken()).thenReturn("internal-token");
        when(filterChain.filter(any())).thenReturn(Mono.empty());

        StepVerifier.create(jwtAuthFilter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(any());
    }
}
