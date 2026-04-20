package com.example.j2n.api_gateway_srv.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.WebFilterChain;

import com.example.j2n.api_gateway_srv.exception.ExpiredTokenException;
import com.example.j2n.api_gateway_srv.exception.InvalidTokenException;
import com.example.j2n.api_gateway_srv.exception.NotRecognizedServiceException;
import com.example.j2n.api_gateway_srv.utils.JwtGeneralUtil;
import com.example.j2n.constants.CommonConst;
import com.example.j2n.utils.RedisUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.impl.DefaultClaims;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
                ReflectionTestUtils.setField(jwtAuthFilter, "bffBaseUrl", "http://localhost:8180");
                jwtAuthFilter.init();
        }

        @Test
        void init_WithInvalidUri() {
                JwtAuthFilter filter = new JwtAuthFilter(jwtGeneralUtil);
                ReflectionTestUtils.setField(filter, "bffBaseUrl", "invalid uri");
                filter.init();
        }

        @Test
        void init_WithUnknownHost() {
                JwtAuthFilter filter = new JwtAuthFilter(jwtGeneralUtil);
                ReflectionTestUtils.setField(filter, "bffBaseUrl", "http://this-host-really-does-not-exist.local");
                filter.init();
        }

        @Test
        void filter_ShouldBypass_WhenPathInWhiteList() {
                List.of("/api/auth/login", "/api/auth/register", "/api/auth/refresh-token", "/api/auth/logout")
                                .forEach(path -> {
                                        var request = MockServerHttpRequest.get(path).build();
                                        var exchange = MockServerWebExchange.from(request);
                                        when(filterChain.filter(any())).thenReturn(Mono.empty());

                                        StepVerifier.create(jwtAuthFilter.filter(exchange, filterChain))
                                                        .verifyComplete();
                                });
        }

        @Test
        void filter_ShouldAllow_WhenIpMatchesAllowedHost() {
                var request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "true")
                                .header("Authorization", "Bearer valid-token")
                                .remoteAddress(new java.net.InetSocketAddress("localhost", 8080))
                                .build();

                var exchange = MockServerWebExchange.from(request);

                Claims claims = new DefaultClaims();
                claims.setId("session123");
                claims.put("user_id", "1");
                claims.put("user_name", "testuser");
                claims.put("role_id", "admin");

                when(jwtGeneralUtil.verify(anyString())).thenReturn(claims);
                when(redisUtil.hasKey(anyString())).thenReturn(true);
                when(jwtGeneralUtil.getInternalToken()).thenReturn("internal-token");
                when(filterChain.filter(any())).thenReturn(Mono.empty());

                StepVerifier.create(jwtAuthFilter.filter(exchange, filterChain))
                                .verifyComplete();

                assertEquals("1", exchange.getAttribute(CommonConst.X_USER_ID));
                assertEquals("testuser", exchange.getAttribute(CommonConst.X_USER_NAME));
                assertEquals("admin", exchange.getAttribute(CommonConst.X_ROLE_ID));
        }

        @SuppressWarnings("unchecked")
        @Test
        void filter_ShouldAllow_WhenRemoteIpEqualsAllowedHost() throws java.net.UnknownHostException {
                java.net.InetAddress mockAddr = mock(java.net.InetAddress.class);
                when(mockAddr.getHostAddress()).thenReturn("my-unresolvable-host.local");
                java.net.InetSocketAddress mockSockAddr = new java.net.InetSocketAddress(mockAddr, 8080);

                var request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "true")
                                .header("Authorization", "Bearer valid-token")
                                .remoteAddress(mockSockAddr)
                                .build();
                var exchange = MockServerWebExchange.from(request);

                Claims claims = new DefaultClaims();
                claims.setId("session123");
                claims.put("user_id", "1");
                claims.put("user_name", "testuser");
                claims.put("role_id", "admin");

                ReflectionTestUtils.setField(jwtAuthFilter, "allowedHost", "my-unresolvable-host.local");
                Set<String> allowedIps = (Set<String>) ReflectionTestUtils.getField(jwtAuthFilter, "allowedIps");
                if (allowedIps != null) {
                        allowedIps.clear();
                }

                when(jwtGeneralUtil.verify(anyString())).thenReturn(claims);
                when(redisUtil.hasKey(anyString())).thenReturn(true);
                when(jwtGeneralUtil.getInternalToken()).thenReturn("internal-token");
                when(filterChain.filter(any())).thenReturn(Mono.empty());

                StepVerifier.create(jwtAuthFilter.filter(exchange, filterChain))
                                .verifyComplete();
        }

        @SuppressWarnings("unchecked")
        @Test
        void filter_ShouldResolveIpAndRetry_WhenIpNotInCache() {
                Set<String> allowedIps = (Set<String>) ReflectionTestUtils.getField(jwtAuthFilter, "allowedIps");
                if (allowedIps != null) {
                        allowedIps.clear();
                }

                var request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "true")
                                .header("Authorization", "Bearer token")
                                .remoteAddress(new java.net.InetSocketAddress("127.0.0.1", 8080))
                                .build();
                var exchange = MockServerWebExchange.from(request);

                Claims claims = new DefaultClaims();
                claims.setId("s1");
                claims.put("user_id", "1");
                claims.put("user_name", "test");
                claims.put("role_id", "user");

                when(jwtGeneralUtil.verify(anyString())).thenReturn(claims);
                when(redisUtil.hasKey(anyString())).thenReturn(true);
                when(filterChain.filter(any())).thenReturn(Mono.empty());

                StepVerifier.create(jwtAuthFilter.filter(exchange, filterChain))
                                .verifyComplete();
        }

        @Test
        void filter_ShouldThrowNotRecognized_WhenNotFromBffHeader() {
                var request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "false")
                                .remoteAddress(new java.net.InetSocketAddress("127.0.0.1", 8080))
                                .build();
                var exchange = MockServerWebExchange.from(request);

                assertThrows(NotRecognizedServiceException.class, () -> jwtAuthFilter.filter(exchange, filterChain));
        }

        @Test
        void filter_ShouldThrowNotRecognized_WhenIpWhitelistingFailed() {
                var request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "true")
                                .remoteAddress(new java.net.InetSocketAddress("192.168.1.5", 8080))
                                .build();
                var exchange = MockServerWebExchange.from(request);

                assertThrows(NotRecognizedServiceException.class, () -> jwtAuthFilter.filter(exchange, filterChain));
        }

        @Test
        void filter_ShouldThrowExpiredToken_WhenTokenExpired() {
                var request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "true")
                                .header("Authorization", "Bearer expired-token")
                                .remoteAddress(new java.net.InetSocketAddress("localhost", 8080))
                                .build();
                var exchange = MockServerWebExchange.from(request);
                when(jwtGeneralUtil.verify(anyString())).thenThrow(new ExpiredJwtException(null, null, "Expired"));

                assertThrows(ExpiredTokenException.class, () -> jwtAuthFilter.filter(exchange, filterChain));
        }

        @Test
        void filter_ShouldThrowInvalidToken_WhenJwtException() {
                var request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "true")
                                .header("Authorization", "Bearer invalid-token")
                                .remoteAddress(new java.net.InetSocketAddress("localhost", 8080))
                                .build();
                var exchange = MockServerWebExchange.from(request);
                when(jwtGeneralUtil.verify(anyString())).thenThrow(new JwtException("Invalid"));

                assertThrows(InvalidTokenException.class, () -> jwtAuthFilter.filter(exchange, filterChain));
        }

        @Test
        void filter_ShouldThrowInvalidToken_WhenGenericException() {
                var request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "true")
                                .header("Authorization", "Bearer token")
                                .remoteAddress(new java.net.InetSocketAddress("localhost", 8080))
                                .build();
                var exchange = MockServerWebExchange.from(request);
                when(jwtGeneralUtil.verify(anyString())).thenThrow(new RuntimeException());

                assertThrows(InvalidTokenException.class, () -> jwtAuthFilter.filter(exchange, filterChain));
        }

        @Test
        void filter_ShouldThrowInvalidToken_WhenSessionIdMissing() {
                var request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "true")
                                .header("Authorization", "Bearer token")
                                .remoteAddress(new java.net.InetSocketAddress("localhost", 8080))
                                .build();
                var exchange = MockServerWebExchange.from(request);

                Claims claims = new DefaultClaims();
                when(jwtGeneralUtil.verify(anyString())).thenReturn(claims);

                assertThrows(InvalidTokenException.class, () -> jwtAuthFilter.filter(exchange, filterChain));
        }

        @Test
        void filter_ShouldThrowExpiredToken_WhenSessionRevoked() {
                var request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "true")
                                .header("Authorization", "Bearer token")
                                .remoteAddress(new java.net.InetSocketAddress("localhost", 8080))
                                .build();
                var exchange = MockServerWebExchange.from(request);

                Claims claims = new DefaultClaims();
                claims.setId("s1");
                when(jwtGeneralUtil.verify(anyString())).thenReturn(claims);
                when(redisUtil.hasKey(anyString())).thenReturn(false);

                assertThrows(ExpiredTokenException.class, () -> jwtAuthFilter.filter(exchange, filterChain));
        }

        @Test
        void filter_ShouldThrowInvalidToken_WhenAuthHeaderMissing() {
                var request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "true")
                                .remoteAddress(new java.net.InetSocketAddress("localhost", 8080))
                                .build();
                var exchange = MockServerWebExchange.from(request);

                assertThrows(InvalidTokenException.class, () -> jwtAuthFilter.filter(exchange, filterChain));
        }

        @Test
        void filter_ShouldThrowInvalidToken_WhenAuthHeaderInvalidFormat() {
                var request = MockServerHttpRequest.get("/api/user/me")
                                .header("FROM-BFF", "true")
                                .header("Authorization", "Basic 123")
                                .remoteAddress(new java.net.InetSocketAddress("localhost", 8080))
                                .build();
                var exchange = MockServerWebExchange.from(request);

                assertThrows(InvalidTokenException.class, () -> jwtAuthFilter.filter(exchange, filterChain));
        }
}
