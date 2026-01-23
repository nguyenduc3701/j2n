package com.example.j2n.api_gateway_srv.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.test.StepVerifier;

import java.net.InetSocketAddress;

class RateLimitConfigTest {

        private RateLimitConfig rateLimitConfig;

        @BeforeEach
        void setUp() {
                rateLimitConfig = new RateLimitConfig();
        }

        @Test
        void ipKeyResolver_ShouldReturnIp() {
                KeyResolver resolver = rateLimitConfig.ipKeyResolver();
                MockServerHttpRequest request = MockServerHttpRequest.get("/")
                                .remoteAddress(new InetSocketAddress("127.0.0.1", 8080))
                                .build();
                MockServerWebExchange exchange = MockServerWebExchange.from(request);

                StepVerifier.create(resolver.resolve(exchange))
                                .expectNext("ip:127.0.0.1")
                                .verifyComplete();
        }

        @Test
        void userKeyResolver_ShouldReturnUserId_WhenPresent() {
                KeyResolver resolver = rateLimitConfig.userKeyResolver();
                MockServerHttpRequest request = MockServerHttpRequest.get("/")
                                .header("X-User-Id", "user123")
                                .build();
                MockServerWebExchange exchange = MockServerWebExchange.from(request);

                StepVerifier.create(resolver.resolve(exchange))
                                .expectNext("user:user123")
                                .verifyComplete();
        }

        @Test
        void userKeyResolver_ShouldReturnAnonymous_WhenUserIdMissing() {
                KeyResolver resolver = rateLimitConfig.userKeyResolver();
                MockServerHttpRequest request = MockServerHttpRequest.get("/")
                                .build();
                MockServerWebExchange exchange = MockServerWebExchange.from(request);

                StepVerifier.create(resolver.resolve(exchange))
                                .expectNext("anonymous")
                                .verifyComplete();
        }
}
