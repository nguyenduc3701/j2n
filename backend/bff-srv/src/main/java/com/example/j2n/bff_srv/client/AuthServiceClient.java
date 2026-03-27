package com.example.j2n.bff_srv.client;

import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.service.response.LoginResponse;
import com.example.j2n.bff_srv.utils.RestClientUtil;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.exception.RetryableException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthServiceClient {
    @Value("${application.access-token.expired-time-seconds}")
    private int accessTokenExpireSeconds;
    @Value("${application.refresh-token.expired-time-days}")
    private int refreshTokenExpireDays;
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";

    private final RestClientUtil restClientUtil;

    public String refresh(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("[BFF-SRV] Calling Auth-Srv to refresh token...");
            String oldRefreshToken = getTokenFromCookie(request, REFRESH_TOKEN);
            if (oldRefreshToken == null) {
                log.warn("[BFF-SRV] No refresh token found in cookies, skipping refresh");
                return null;
            }
            Map<String, String> authHeaders = Map.of(
                    "FROM-BFF", "true",
                    "X-Refresh-Token", oldRefreshToken);
            BaseResponse<LoginResponse> responseObj = restClientUtil.requestAuth(
                    GatewayPath.AUTH_REFRESH_TOKEN_PATH,
                    HttpMethod.POST,
                    null,
                    authHeaders,
                    new ParameterizedTypeReference<BaseResponse<LoginResponse>>() {
                    });

            if (responseObj != null && responseObj.getData() != null) {
                LoginResponse data = responseObj.getData();
                updateResponseCredentials(response, data.getAccessToken(), data.getRefreshToken());
                return data.getAccessToken();
            }
        } catch (Exception e) {
            log.error("[BFF-SRV] Silent refresh failed: {}", e.getMessage());
            throw new RetryableException(BaseMessageEnum.NON_RETRYABLE);
        }
        return null;
    }

    public String refresh() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return refresh(attr != null ? attr.getRequest() : null, attr != null ? attr.getResponse() : null);
    }

    public String getTokenFromCookie(HttpServletRequest request, String type) {
        if (request == null)
            return null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (type.equals(cookie.getName()))
                    return cookie.getValue();
            }
        }
        return null;
    }

    public String getTokenFromCookie(String type) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return getTokenFromCookie(attr != null ? attr.getRequest() : null, type);
    }

    public void updateResponseCredentials(HttpServletResponse response, String newAccess, String newRefresh) {
        if (response != null) {
            response.addHeader("X-New-Access-Token", newAccess);
            ResponseCookie accessCookie = ResponseCookie.from(ACCESS_TOKEN, newAccess)
                    .httpOnly(true)
                    .secure(false) // Để true nếu dùng HTTPS, false nếu test localhost
                    .path("/")
                    .maxAge(accessTokenExpireSeconds)
                    .sameSite("Lax")
                    .build();

            ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_TOKEN, newRefresh)
                    .httpOnly(true)
                    .secure(false)
                    .path("/") // Khớp với path cũ
                    .maxAge(refreshTokenExpireDays * 24 * 3600)
                    .sameSite("Lax")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
            log.info("[BFF-SRV] Credentials updated in Current Response Context");
        }
    }

    public void updateResponseCredentials(String newAccess, String newRefresh) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        updateResponseCredentials(attr != null ? attr.getResponse() : null, newAccess, newRefresh);
    }
}