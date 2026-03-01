package com.example.j2n.api_gateway_srv.filter;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
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
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter implements WebFilter {
    @Autowired
    private RedisUtil redisUtil;
    private final JwtGeneralUtil jwtGeneralUtil;

    // Các hằng số định nghĩa tên Header sẽ gửi xuống Microservices phía sau
    private final String X_INTERNAL_TOKEN = "X-Internal-Token";
    private final String X_USER_ID = "X-User-Id";
    private final String X_USER_NAME = "X-User-Name";
    private final String X_ROLE_ID = "X-Role-Id";

    // Các key tương ứng để lấy dữ liệu từ nội dung (Claims) của JWT
    private final String KEY_USER_ID = "user_id";
    private final String KEY_USER_NAME = "user_name";
    private final String KEY_ROLE_ID = "role_id";

    private final List<String> BY_PASS_AUTH_LIST = List.of("/api/auth/login", "/api/auth/register",
            "/api/auth/refresh-token", "/api/auth/logout");

    @Override
    @SneakyThrows
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        // 1. Lấy đường dẫn request hiện tại
        String path = exchange.getRequest().getPath().value();

        // 2. Kiểm tra nếu path nằm trong danh sách bypass thì cho đi tiếp luôn
        if (BY_PASS_AUTH_LIST.contains(path)) {
            log.info("[GATEWAY] Bypass auth filter");
            return chain.filter(exchange);
        }

        // 3. Kiểm tra Header "FROM-BFF" để đảm bảo request đến từ nguồn tin cậy (BFF)
        String fromBff = exchange.getRequest().getHeaders().getFirst("FROM-BFF");
        if (!Boolean.parseBoolean(fromBff)) {
            log.warn("[GATEWAY] Not recognized service");
            throw new NotRecognizedServiceException();
        }

        // 4. Lấy và kiểm tra định dạng Header Authorization (Bearer Token)
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("[GATEWAY] Invalid token");
            throw new InvalidTokenException();
        }

        // 5. Trích xuất token và thực hiện verify (giải mã/kiểm tra chữ ký)
        String token = authHeader.substring(7);
        Claims claims;
        try {
            claims = jwtGeneralUtil.verify(token);
        } catch (ExpiredJwtException e) {
            log.warn("[GATEWAY] Token expired");
            throw new ExpiredTokenException();
        } catch (JwtException e) {
            log.warn("[GATEWAY] Invalid token");
            throw new InvalidTokenException();
        } catch (Exception e) {
            log.warn("[GATEWAY] Invalid token");
            throw new InvalidTokenException();
        }

        String sessionId = claims.getId();
        if (sessionId == null) {
            log.warn("[GATEWAY] Invalid token");
            throw new InvalidTokenException();
        }

        // Kiểm tra sự tồn tại của session trong Redis
        // Key: auth:session:{sessionId}
        String sessionKey = CommonConst.AUTH_SESSION_PREFIX + sessionId;
        if (!redisUtil.hasKey(sessionKey)) {
            log.warn("[GATEWAY] Session has been revoked/logged out: {}", sessionId);
            throw new ExpiredTokenException();
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
        log.debug("[GATEWAY] Set exchange attributes: userId={}, userName={}", userId, userName);

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

        log.info("[GATEWAY] User {} has been authenticated", userId);
        return chain.filter(mutatedExchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }
}