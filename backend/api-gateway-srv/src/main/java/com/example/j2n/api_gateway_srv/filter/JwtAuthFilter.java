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
import java.net.URI;
import java.net.InetAddress;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;
import java.net.UnknownHostException;

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

    @Value("${api-gateway.bff.base-url}")
    private String bffBaseUrl;

    private final Set<String> allowedIps = new HashSet<>();
    private String allowedHost;

    @PostConstruct
    public void init() {
        try {
            URI uri = new URI(bffBaseUrl);
            this.allowedHost = uri.getHost();
            log.info("[GATEWAY-AUTH] Initializing IP Whitelisting for BFF host: {}", allowedHost);

            // Thử resolve IP ngay lúc startup
            resolveIp();
        } catch (Exception e) {
            log.error("[GATEWAY-AUTH] Failed to parse BFF base URL: {}", bffBaseUrl, e);
        }
    }

    private void resolveIp() {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(allowedHost);
            for (InetAddress addr : addresses) {
                allowedIps.add(addr.getHostAddress());
            }
            log.info("[GATEWAY-AUTH] Allowed IPs for BFF: {}", allowedIps);
        } catch (UnknownHostException e) {
            log.warn("[GATEWAY-AUTH] Could not resolve BFF host {} to IP yet. Will retry during filter if needed.",
                    allowedHost);
        }
    }

    // Các key tương ứng để lấy dữ liệu từ nội dung (Claims) của JWT
    private final String KEY_USER_ID = "user_id";
    private final String KEY_USER_NAME = "user_name";
    private final String KEY_ROLE_ID = "role_id";

    private final List<String> BY_PASS_AUTH_LIST = List.of("/api/auth/login", "/api/auth/register",
            "/api/auth/refresh-token", "/api/auth/logout");

    @Override
    @SneakyThrows
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        // 1. Kiểm tra nếu path nằm trong danh sách bypass thì cho đi tiếp luôn
        String path = exchange.getRequest().getPath().value();
        if (BY_PASS_AUTH_LIST.contains(path)) {
            log.info("[GATEWAY] Bypass auth filter for path: {}", path);
            return chain.filter(exchange);
        }

        // 2. Thực hiện kiểm tra nguồn request (IP, Header BFF) và Header Authorization
        validateRequest(exchange);

        // 5. Trích xuất token và thực hiện verify (giải mã/kiểm tra chữ ký)
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
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

        // 3. Kiểm tra Session trong Redis
        validateSession(claims);

        // 4. Lưu thông tin người dùng vào Exchange Attributes
        populateUserData(exchange, claims);

        // 5. Gán thông tin User vào Request Headers cho các Service phía sau
        ServerWebExchange mutatedExchange = mutateRequest(exchange, claims);

        // 6. Tạo đối tượng Authentication cho Spring Security Context
        String userId = claims.get(KEY_USER_ID, String.class);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null,
                Collections.emptyList());

        // 11. Tiếp tục chuỗi filter và ghi đè SecurityContext với thông tin user đã xác
        // thực

        log.info("[GATEWAY] User {} has been authenticated", userId);
        return chain.filter(mutatedExchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }

    private void validateRequest(ServerWebExchange exchange) {
        // 1. Kết hợp IP Whitelisting và Header Check: Đảm bảo request đến từ BFF tin
        // cậy
        String fromBff = exchange.getRequest().getHeaders().getFirst("FROM-BFF");
        String remoteIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();

        // A. Kiểm tra Header
        if (!Boolean.parseBoolean(fromBff)) {
            log.warn("[GATEWAY] Missing or invalid FROM-BFF header from IP: {}", remoteIp);
            throw new NotRecognizedServiceException();
        }

        // B. Kiểm tra IP (IP Whitelisting)
        if (!allowedIps.contains(remoteIp)) {
            resolveIp();
        }

        if (!allowedIps.contains(remoteIp) && !remoteIp.equals(allowedHost)) {
            log.warn("[GATEWAY] IP Whitelisting failed for IP: {}. Allowed BFF host: {}", remoteIp, allowedHost);
            throw new NotRecognizedServiceException();
        }

        // 2. Lấy và kiểm tra định dạng Header Authorization (Bearer Token)
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("[GATEWAY] Invalid token header format or missing");
            throw new InvalidTokenException();
        }
    }

    private void validateSession(Claims claims) {
        String sessionId = claims.getId();
        if (sessionId == null) {
            log.warn("[GATEWAY] Invalid token: Missing JTI/Session ID");
            throw new InvalidTokenException();
        }

        // Kiểm tra sự tồn tại của session trong Redis
        String sessionKey = CommonConst.AUTH_SESSION_PREFIX + sessionId;
        if (!redisUtil.hasKey(sessionKey)) {
            log.warn("[GATEWAY] Session has been revoked or expired in Redis: {}", sessionId);
            throw new ExpiredTokenException();
        }
    }

    private void populateUserData(ServerWebExchange exchange, Claims claims) {
        String userId = claims.get(KEY_USER_ID, String.class);
        String userName = claims.get(KEY_USER_NAME, String.class);
        String roleId = claims.get(KEY_ROLE_ID, String.class);

        exchange.getAttributes().put(CommonConst.X_USER_ID, userId);
        exchange.getAttributes().put(CommonConst.X_USER_NAME, userName);
        exchange.getAttributes().put(CommonConst.X_ROLE_ID, roleId);

        log.debug("[GATEWAY] Populated user data in exchange attributes for userId: {}", userId);
    }

    private ServerWebExchange mutateRequest(ServerWebExchange exchange, Claims claims) {
        String userId = claims.get(KEY_USER_ID, String.class);
        String userName = claims.get(KEY_USER_NAME, String.class);
        String roleId = claims.get(KEY_ROLE_ID, String.class);

        var requestBuilder = exchange.getRequest().mutate()
                .header(CommonConst.X_USER_ID, userId)
                .header(CommonConst.X_USER_NAME, userName)
                .header(CommonConst.X_INTERNAL_TOKEN, jwtGeneralUtil.getInternalToken())
                .header(CommonConst.X_ROLE_ID, roleId);

        return exchange.mutate()
                .request(requestBuilder.build())
                .build();
    }
}