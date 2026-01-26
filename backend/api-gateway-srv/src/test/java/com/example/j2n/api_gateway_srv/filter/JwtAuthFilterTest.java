package com.example.j2n.api_gateway_srv.filter;

import com.example.j2n.api_gateway_srv.exception.ExpiredTokenException;
import com.example.j2n.api_gateway_srv.exception.InvalidTokenException;
import com.example.j2n.api_gateway_srv.exception.NotRecognizedServiceException;
import com.example.j2n.api_gateway_srv.utils.JwtGeneralUtil;
import com.example.j2n.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

        @Mock
        private JwtGeneralUtil jwtGeneralUtil;

        @Mock
        private RedisUtil redisUtil;

        @Mock
        private WebFilterChain filterChain;

        private JwtAuthFilter jwtAuthFilter;

        @BeforeEach
        void setUp() {
                jwtAuthFilter = new JwtAuthFilter(jwtGeneralUtil);
                ReflectionTestUtils.setField(jwtAuthFilter, "redisUtil", redisUtil);
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
        void filter_ShouldThrowNotRecognized_WhenNotFromBff() {
                MockServerHttpRequest request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "false")
                                .build();
                MockServerWebExchange exchange = MockServerWebExchange.from(request);

                assertThrows(NotRecognizedServiceException.class, () -> jwtAuthFilter.filter(exchange, filterChain));
        }

        @Test
        void filter_ShouldThrowInvalidToken_WhenAuthHeaderMissing() {
                MockServerHttpRequest request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "true")
                                .build();
                MockServerWebExchange exchange = MockServerWebExchange.from(request);

                assertThrows(InvalidTokenException.class, () -> jwtAuthFilter.filter(exchange, filterChain));
        }

        @Test
        void filter_ShouldThrowInvalidToken_WhenAuthHeaderNotBearer() {
                MockServerHttpRequest request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "true")
                                .header("Authorization", "Basic 123")
                                .build();
                MockServerWebExchange exchange = MockServerWebExchange.from(request);

                assertThrows(InvalidTokenException.class, () -> jwtAuthFilter.filter(exchange, filterChain));
        }

        @Test
        void filter_ShouldThrowExpiredToken_WhenTokenExpired() {
                MockServerHttpRequest request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "true")
                                .header("Authorization", "Bearer expired-token")
                                .build();
                MockServerWebExchange exchange = MockServerWebExchange.from(request);
                when(jwtGeneralUtil.verify(anyString())).thenThrow(new ExpiredJwtException(null, null, "Expired"));

                assertThrows(ExpiredTokenException.class, () -> jwtAuthFilter.filter(exchange, filterChain));
        }

        @Test
        void filter_ShouldThrowInvalidToken_WhenJwtException() {
                MockServerHttpRequest request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "true")
                                .header("Authorization", "Bearer invalid-token")
                                .build();
                MockServerWebExchange exchange = MockServerWebExchange.from(request);
                when(jwtGeneralUtil.verify(anyString())).thenThrow(new JwtException("Invalid"));

                assertThrows(InvalidTokenException.class, () -> jwtAuthFilter.filter(exchange, filterChain));
        }

        @Test
        void filter_ShouldThrowInvalidToken_WhenGenericException() {
                MockServerHttpRequest request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "true")
                                .header("Authorization", "Bearer invalid-token")
                                .build();
                MockServerWebExchange exchange = MockServerWebExchange.from(request);
                when(jwtGeneralUtil.verify(anyString())).thenThrow(new RuntimeException("Error"));

                assertThrows(InvalidTokenException.class, () -> jwtAuthFilter.filter(exchange, filterChain));
        }

        @Test
        void filter_ShouldThrowInvalidToken_WhenSessionIdMissing() {
                MockServerHttpRequest request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "true")
                                .header("Authorization", "Bearer valid-token")
                                .build();
                MockServerWebExchange exchange = MockServerWebExchange.from(request);

                Claims claims = new DefaultClaims();
                when(jwtGeneralUtil.verify("valid-token")).thenReturn(claims);

                assertThrows(InvalidTokenException.class, () -> jwtAuthFilter.filter(exchange, filterChain));
        }

        @Test
        void filter_ShouldThrowExpiredToken_WhenSessionRevoked() {
                MockServerHttpRequest request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "true")
                                .header("Authorization", "Bearer valid-token")
                                .build();
                MockServerWebExchange exchange = MockServerWebExchange.from(request);

                Claims claims = new DefaultClaims();
                claims.setId("session123");
                when(jwtGeneralUtil.verify("valid-token")).thenReturn(claims);
                when(redisUtil.hasKey(anyString())).thenReturn(false);

                assertThrows(ExpiredTokenException.class, () -> jwtAuthFilter.filter(exchange, filterChain));
        }

        @Test
        void filter_ShouldProceed_WhenTokenAndSessionValid() {
                MockServerHttpRequest request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "true")
                                .header("Authorization", "Bearer valid-token")
                                .build();
                MockServerWebExchange exchange = MockServerWebExchange.from(request);

                Claims claims = new DefaultClaims();
                claims.setId("session123");
                claims.put("user_id", "1");
                claims.put("user_name", "testuser");
                claims.put("role_id", "admin");

                when(jwtGeneralUtil.verify("valid-token")).thenReturn(claims);
                when(redisUtil.hasKey(anyString())).thenReturn(true);
                when(jwtGeneralUtil.getInternalToken()).thenReturn("internal-token");
                when(filterChain.filter(any())).thenReturn(Mono.empty());

                StepVerifier.create(jwtAuthFilter.filter(exchange, filterChain))
                                .verifyComplete();

                verify(filterChain).filter(any());
        }
}
