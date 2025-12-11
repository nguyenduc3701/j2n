package com.example.j2n.api_gateway_srv.filter;

import com.example.j2n.api_gateway_srv.utils.JwtGeneralUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebFilter;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.List;
import org.springframework.core.io.buffer.DataBuffer;

import com.example.j2n.enums.MessageEnum;
import com.example.j2n.utils.ResponseFactory;
import com.example.j2n.dto.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements WebFilter {
    private final JwtGeneralUtil jwtGeneralUtil;
    private final ObjectMapper objectMapper;
    private final String X_USER_ID = "X-User-Id";
    private final String X_USER_NAME = "X-User-Name";
    private final String X_ROLE_ID = "X-Role-Id";
    private final String KEY_USER_ID = "user_id";
    private final String KEY_USER_NAME = "user_name";
    private final String KEY_ROLE_ID = "role_id";
    private final List<String> BY_PASS_AUTH_LIST = List.of("/api/auth/login", "/api/auth/register");

    @Override
    @SneakyThrows
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        if (BY_PASS_AUTH_LIST.contains(path)) {
            return chain.filter(exchange);
        }

        String fromBff = exchange.getRequest().getHeaders().getFirst("FROM-BFF");
        if (!Boolean.parseBoolean(fromBff)) {
            BaseResponse<Object> response = ResponseFactory.error(MessageEnum.SERVICE_NOT_RECOGNIZED);
            byte[] bytes = objectMapper.writeValueAsBytes(response);
            DataBuffer buffer = exchange.getResponse()
                    .bufferFactory()
                    .wrap(bytes);
            exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
            return exchange.getResponse().writeWith(Mono.just(buffer)).then(Mono.empty());
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            BaseResponse<Object> response = ResponseFactory.error(MessageEnum.TOKEN_INVALID);
            byte[] bytes = objectMapper.writeValueAsBytes(response);
            DataBuffer buffer = exchange.getResponse()
                    .bufferFactory()
                    .wrap(bytes);
            exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
            return exchange.getResponse().writeWith(Mono.just(buffer)).then(Mono.empty());
        }

        String token = authHeader.substring(7);
        Claims claims;
        try {
            claims = jwtGeneralUtil.verify(token);
        } catch (Exception e) {
            BaseResponse<Object> response = ResponseFactory.error(MessageEnum.TOKEN_INVALID);
            byte[] bytes = objectMapper.writeValueAsBytes(response);
            DataBuffer buffer = exchange.getResponse()
                    .bufferFactory()
                    .wrap(bytes);
            exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
            return exchange.getResponse().writeWith(Mono.just(buffer)).then(Mono.empty());
        }

        // Lấy giá trị từ JWT
        String userId = claims.get(KEY_USER_ID, String.class);
        String userName = claims.get(KEY_USER_NAME, String.class);
        String roleId = claims.get(KEY_ROLE_ID, String.class);

        // Set vào attributes
        exchange.getAttributes().put(X_USER_ID, userId);
        exchange.getAttributes().put(X_USER_NAME, userName);
        exchange.getAttributes().put(X_ROLE_ID, roleId);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null,
                Collections.emptyList());

        // Build request mới chỉ thêm header nếu không null
        var requestBuilder = exchange.getRequest().mutate()
                .header(X_USER_ID, userId)
                .header(X_USER_NAME, userName)
                .header(X_ROLE_ID, roleId);

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(requestBuilder.build())
                .build();

        return chain.filter(mutatedExchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }
}
