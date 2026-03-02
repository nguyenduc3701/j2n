package com.example.j2n.api_gateway_srv.controller;

import com.example.j2n.constants.CommonConst;
import com.example.j2n.api_gateway_srv.utils.ReportApiDiscovery;
import com.example.j2n.api_gateway_srv.service.DynamicGrpcInvoker;
import com.example.j2n.api_gateway_srv.utils.GrpcFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/report")
@Slf4j
public class ReportForwardController {

    private final ReportApiDiscovery discovery;
    private final DynamicGrpcInvoker invoker;
    private final GrpcFactory grpcFactory;
    private final RedisRateLimiter redisRateLimiter;
    private final KeyResolver userKeyResolver;

    public ReportForwardController(
            ReportApiDiscovery discovery,
            DynamicGrpcInvoker invoker,
            GrpcFactory grpcFactory,
            RedisRateLimiter redisRateLimiter,
            @Qualifier("userKeyResolver") KeyResolver userKeyResolver) {
        this.discovery = discovery;
        this.invoker = invoker;
        this.grpcFactory = grpcFactory;
        this.redisRateLimiter = redisRateLimiter;
        this.userKeyResolver = userKeyResolver;
    }

    @RequestMapping(value = "/**", method = { RequestMethod.POST,
            RequestMethod.PATCH }, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> forwardMultipart(ServerWebExchange exchange) {
        return checkRateLimit(exchange)
                .then(getValidatedMethodName(exchange))
                .flatMap(methodName -> exchange.getMultipartData()
                        .flatMap(grpcFactory::convertMultipartToJson)
                        .flatMap(jsonBody -> performInvoke(methodName, jsonBody, exchange)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.PATCH, RequestMethod.DELETE })
    public Mono<ResponseEntity<String>> forwardRegular(
            @RequestBody(required = false) String body,
            ServerWebExchange exchange) {

        return checkRateLimit(exchange)
                .then(getValidatedMethodName(exchange))
                .flatMap(methodName -> performInvoke(methodName, body != null ? body : "{}", exchange))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    private Mono<ResponseEntity<String>> performInvoke(String methodName, String jsonBody, ServerWebExchange exchange) {
        String userId = exchange.getRequest().getHeaders().getFirst(CommonConst.X_USER_ID);
        String userName = exchange.getRequest().getHeaders().getFirst(CommonConst.X_USER_NAME);
        String roleId = exchange.getRequest().getHeaders().getFirst(CommonConst.X_ROLE_ID);

        return grpcFactory.invokeAndWrap(
                () -> invoker.invoke(methodName, jsonBody, userId, userName, roleId),
                methodName);
    }

    private Mono<String> getValidatedMethodName(ServerWebExchange exchange) {
        String fullPath = exchange.getRequest().getPath().value();
        log.info("[FORWARD-CONTROLLER] Incoming request for path: {}", fullPath);

        String methodName = discovery.getMethodName(fullPath);
        if (methodName == null) {
            log.warn("[FORWARD-CONTROLLER] No mapping found for path: {}", fullPath);
            return Mono.empty();
        }
        return Mono.just(methodName);
    }

    private Mono<Void> checkRateLimit(ServerWebExchange exchange) {
        return userKeyResolver.resolve(exchange)
                .flatMap(key -> redisRateLimiter.isAllowed("report-srv", key))
                .flatMap(response -> {
                    if (!response.isAllowed()) {
                        log.warn("[RATE-LIMIT] Request limit exceeded for report service");
                        return Mono.error(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS));
                    }
                    return Mono.empty();
                });
    }
}
