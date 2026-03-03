package com.example.j2n.api_gateway_srv.controller;

import com.example.j2n.api_gateway_srv.service.DynamicGrpcInvoker;
import com.example.j2n.api_gateway_srv.utils.GrpcFactory;
import com.example.j2n.api_gateway_srv.utils.ReportApiDiscovery;
import com.example.j2n.constants.CommonConst;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.Part;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportForwardControllerTest {

        @Mock
        private ReportApiDiscovery discovery;
        @Mock
        private DynamicGrpcInvoker invoker;
        @Mock
        private GrpcFactory grpcFactory;
        @Mock
        private RedisRateLimiter redisRateLimiter;
        @Mock
        private KeyResolver userKeyResolver;

        private ReportForwardController controller;

        @BeforeEach
        void setUp() {
                controller = new ReportForwardController(discovery, invoker, grpcFactory, redisRateLimiter,
                                userKeyResolver);
        }

        @Test
        void forwardRegular_Success() {
                MockServerHttpRequest request = MockServerHttpRequest.post("/api/report/dashboard")
                                .header(CommonConst.X_USER_ID, "1")
                                .header(CommonConst.X_USER_NAME, "test")
                                .header(CommonConst.X_ROLE_ID, "admin")
                                .build();
                MockServerWebExchange exchange = MockServerWebExchange.from(request);

                when(userKeyResolver.resolve(any())).thenReturn(Mono.just("user1"));
                RedisRateLimiter.Response rateLimitResponse = new RedisRateLimiter.Response(true, Map.of());
                when(redisRateLimiter.isAllowed(anyString(), anyString())).thenReturn(Mono.just(rateLimitResponse));
                when(discovery.getMethodName(anyString())).thenReturn("GetDashboardReport");
                when(grpcFactory.invokeAndWrap(any(), anyString())).thenAnswer(invocation -> {
                        try {
                                ((java.util.concurrent.Callable<?>) invocation.getArgument(0)).call();
                        } catch (Exception e) {
                        }
                        return Mono.just(ResponseEntity.ok("success"));
                });

                StepVerifier.create(controller.forwardRegular("{}", exchange))
                                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK
                                                && "success".equals(response.getBody()))
                                .verifyComplete();
        }

        @Test
        void forwardRegular_VariousMethods() {
                for (String method : List.of("PUT", "PATCH", "DELETE", "GET")) {
                        MockServerHttpRequest request = MockServerHttpRequest
                                        .method(org.springframework.http.HttpMethod.valueOf(method),
                                                        "/api/report/dashboard")
                                        .header(CommonConst.X_USER_ID, "1")
                                        .build();
                        MockServerWebExchange exchange = MockServerWebExchange.from(request);

                        when(userKeyResolver.resolve(any())).thenReturn(Mono.just("user1"));
                        when(redisRateLimiter.isAllowed(anyString(), anyString()))
                                        .thenReturn(Mono.just(new RedisRateLimiter.Response(true, Map.of())));
                        when(discovery.getMethodName(anyString())).thenReturn("GetDashboardReport");
                        when(grpcFactory.invokeAndWrap(any(), anyString())).thenAnswer(invocation -> {
                                try {
                                        ((java.util.concurrent.Callable<?>) invocation.getArgument(0)).call();
                                } catch (Exception e) {
                                }
                                return Mono.just(ResponseEntity.ok("success"));
                        });

                        StepVerifier.create(controller.forwardRegular(null, exchange))
                                        .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK)
                                        .verifyComplete();
                }
        }

        @Test
        void forwardRegular_RateLimited() {
                MockServerHttpRequest request = MockServerHttpRequest.post("/api/report/dashboard").build();
                MockServerWebExchange exchange = MockServerWebExchange.from(request);

                when(userKeyResolver.resolve(any())).thenReturn(Mono.just("user1"));
                RedisRateLimiter.Response rateLimitResponse = new RedisRateLimiter.Response(false, Map.of());
                when(redisRateLimiter.isAllowed(anyString(), anyString())).thenReturn(Mono.just(rateLimitResponse));

                StepVerifier.create(controller.forwardRegular("{}", exchange))
                                .expectError(ResponseStatusException.class)
                                .verify();
        }

        @Test
        void forwardRegular_NotFound() {
                MockServerHttpRequest request = MockServerHttpRequest.post("/api/report/unknown").build();
                MockServerWebExchange exchange = MockServerWebExchange.from(request);

                when(userKeyResolver.resolve(any())).thenReturn(Mono.just("user1"));
                RedisRateLimiter.Response rateLimitResponse = new RedisRateLimiter.Response(true, Map.of());
                when(redisRateLimiter.isAllowed(anyString(), anyString())).thenReturn(Mono.just(rateLimitResponse));
                when(discovery.getMethodName(anyString())).thenReturn(null);

                StepVerifier.create(controller.forwardRegular("{}", exchange))
                                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NOT_FOUND)
                                .verifyComplete();
        }

        @Test
        void forwardMultipart_Success() {
                MockServerHttpRequest request = MockServerHttpRequest.post("/api/report/upload")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .header(CommonConst.X_USER_ID, "1")
                                .build();

                ServerWebExchange exchange = mock(ServerWebExchange.class);
                when(exchange.getRequest()).thenReturn(request);

                MultiValueMap<String, Part> multipartData = new LinkedMultiValueMap<>();
                when(exchange.getMultipartData()).thenReturn(Mono.just(multipartData));

                when(userKeyResolver.resolve(any())).thenReturn(Mono.just("user1"));
                RedisRateLimiter.Response rateLimitResponse = new RedisRateLimiter.Response(true, Map.of());
                when(redisRateLimiter.isAllowed(anyString(), anyString())).thenReturn(Mono.just(rateLimitResponse));
                when(discovery.getMethodName(anyString())).thenReturn("UploadReport");

                when(grpcFactory.convertMultipartToJson(any())).thenReturn(Mono.just("{\"file\":\"base64\"}"));
                when(grpcFactory.invokeAndWrap(any(), anyString())).thenAnswer(invocation -> {
                        try {
                                ((java.util.concurrent.Callable<?>) invocation.getArgument(0)).call();
                        } catch (Exception e) {
                        }
                        return Mono.just(ResponseEntity.ok("success"));
                });

                StepVerifier.create(controller.forwardMultipart(exchange))
                                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK)
                                .verifyComplete();
        }

        @Test
        void forwardMultipart_Patch_Success() {
                MockServerHttpRequest request = MockServerHttpRequest.patch("/api/report/upload")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .header(CommonConst.X_USER_ID, "1")
                                .build();

                ServerWebExchange exchange = mock(ServerWebExchange.class);
                when(exchange.getRequest()).thenReturn(request);

                MultiValueMap<String, Part> multipartData = new LinkedMultiValueMap<>();
                when(exchange.getMultipartData()).thenReturn(Mono.just(multipartData));

                when(userKeyResolver.resolve(any())).thenReturn(Mono.just("user1"));
                when(redisRateLimiter.isAllowed(anyString(), anyString()))
                                .thenReturn(Mono.just(new RedisRateLimiter.Response(true, Map.of())));
                when(discovery.getMethodName(anyString())).thenReturn("UploadReport");

                when(grpcFactory.convertMultipartToJson(any())).thenReturn(Mono.just("{}"));
                when(grpcFactory.invokeAndWrap(any(), anyString())).thenAnswer(invocation -> {
                        try {
                                ((java.util.concurrent.Callable<?>) invocation.getArgument(0)).call();
                        } catch (Exception e) {
                        }
                        return Mono.just(ResponseEntity.ok("success"));
                });

                StepVerifier.create(controller.forwardMultipart(exchange))
                                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK)
                                .verifyComplete();
        }

        @Test
        void forwardMultipart_NotFound() {
                MockServerHttpRequest request = MockServerHttpRequest.post("/api/report/unknown")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .build();
                ServerWebExchange exchange = mock(ServerWebExchange.class);
                when(exchange.getRequest()).thenReturn(request);

                when(userKeyResolver.resolve(any())).thenReturn(Mono.just("user1"));
                RedisRateLimiter.Response rateLimitResponse = new RedisRateLimiter.Response(true, Map.of());
                when(redisRateLimiter.isAllowed(anyString(), anyString())).thenReturn(Mono.just(rateLimitResponse));
                when(discovery.getMethodName(anyString())).thenReturn(null);

                StepVerifier.create(controller.forwardMultipart(exchange))
                                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NOT_FOUND)
                                .verifyComplete();
        }

        @Test
        void forwardMultipart_RateLimited() {
                MockServerHttpRequest request = MockServerHttpRequest.post("/api/report/upload")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .build();
                ServerWebExchange exchange = mock(ServerWebExchange.class);
                when(exchange.getRequest()).thenReturn(request);

                when(userKeyResolver.resolve(any())).thenReturn(Mono.just("user1"));
                RedisRateLimiter.Response rateLimitResponse = new RedisRateLimiter.Response(false, Map.of());
                when(redisRateLimiter.isAllowed(anyString(), anyString())).thenReturn(Mono.just(rateLimitResponse));

                StepVerifier.create(controller.forwardMultipart(exchange))
                                .expectError(ResponseStatusException.class)
                                .verify();
        }
}
