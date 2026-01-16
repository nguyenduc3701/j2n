package com.example.j2n.api_gateway_srv.filter;

import com.example.j2n.api_gateway_srv.constant.MessageEnum;
import com.example.j2n.api_gateway_srv.utils.JwtGeneralUtil;
import org.springframework.http.HttpHeaders;
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

import com.example.j2n.utils.ResponseFactory;
import com.example.j2n.dto.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements WebFilter {
    private final JwtGeneralUtil jwtGeneralUtil;
    private final ObjectMapper objectMapper;

    // Các hằng số định nghĩa tên Header sẽ gửi xuống Microservices phía sau
    private final String X_INTERNAL_TOKEN = "X-Internal-Token";
    private final String X_USER_ID = "X-User-Id";
    private final String X_USER_NAME = "X-User-Name";
    private final String X_ROLE_ID = "X-Role-Id";

    // Các key tương ứng để lấy dữ liệu từ nội dung (Claims) của JWT
    private final String KEY_USER_ID = "user_id";
    private final String KEY_USER_NAME = "user_name";
    private final String KEY_ROLE_ID = "role_id";

    // Danh sách các API công khai không cần kiểm tra Token
    private final List<String> BY_PASS_AUTH_LIST = List.of("/api/auth/login", "/api/auth/register");

    @Override
    @SneakyThrows
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        // 1. Lấy đường dẫn request hiện tại
        String path = exchange.getRequest().getPath().value();

        // 2. Kiểm tra nếu path nằm trong danh sách bypass thì cho đi tiếp luôn
        if (BY_PASS_AUTH_LIST.contains(path)) {
            return chain.filter(exchange);
        }

        // 3. Kiểm tra Header "FROM-BFF" để đảm bảo request đến từ nguồn tin cậy (BFF)
        String fromBff = exchange.getRequest().getHeaders().getFirst("FROM-BFF");
        if (!Boolean.parseBoolean(fromBff)) {
            return setErrorResponse(exchange, MessageEnum.SERVICE_NOT_RECOGNIZED);
        }

        // 4. Lấy và kiểm tra định dạng Header Authorization (Bearer Token)
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return setErrorResponse(exchange, MessageEnum.TOKEN_INVALID);
        }

        // 5. Trích xuất token và thực hiện verify (giải mã/kiểm tra chữ ký)
        String token = authHeader.substring(7);
        Claims claims;
        try {
            claims = jwtGeneralUtil.verify(token);
        } catch (Exception e) {
            // Nếu token hết hạn hoặc sai chữ ký thì trả về lỗi ngay
            return setErrorResponse(exchange, MessageEnum.TOKEN_INVALID);
        }

        // 6. Lấy thông tin người dùng từ nội dung Token (Claims)
        String userId = claims.get(KEY_USER_ID, String.class);
        String userName = claims.get(KEY_USER_NAME, String.class);
        String roleId = claims.get(KEY_ROLE_ID, String.class);

        // 7. Lưu thông tin vào Attributes của exchange (có thể dùng để xử lý nội bộ
        // trong Gateway)
        exchange.getAttributes().put(X_USER_ID, userId);
        exchange.getAttributes().put(X_USER_NAME, userName);
        exchange.getAttributes().put(X_ROLE_ID, roleId);

        // 8. Tạo đối tượng Authentication để tích hợp với Spring Security Context
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null,
                Collections.emptyList());

        // 9. Mutate (biến đổi) request: Thêm các thông tin user vào Header
        // Mục đích: Để các service phía sau lấy được thông tin user mà không cần parse
        // lại JWT
        var requestBuilder = exchange.getRequest().mutate()
                // .headers(headers -> headers.remove(HttpHeaders.AUTHORIZATION))
                .header(X_USER_ID, userId)
                .header(X_USER_NAME, userName)
                .header(X_INTERNAL_TOKEN, jwtGeneralUtil.getInternalToken())
                .header(X_ROLE_ID, roleId);

        // 10. Tạo exchange mới với request đã được cập nhật Header
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(requestBuilder.build())
                .build();

        // 11. Tiếp tục chuỗi filter và ghi đè SecurityContext với thông tin user đã xác
        // thực
        return chain.filter(mutatedExchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }

    /**
     * Hàm hỗ trợ ghi đè response trả về lỗi dưới dạng JSON khi filter chặn lại
     */
    @SneakyThrows
    private Mono<Void> setErrorResponse(ServerWebExchange exchange, MessageEnum messageEnum) {
        BaseResponse<Object> response = ResponseFactory.error(messageEnum);
        byte[] bytes = objectMapper.writeValueAsBytes(response);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        return exchange.getResponse().writeWith(Mono.just(buffer)).then(Mono.empty());
    }
}