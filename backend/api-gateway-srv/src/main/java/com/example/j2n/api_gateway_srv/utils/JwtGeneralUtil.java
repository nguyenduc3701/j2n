package com.example.j2n.api_gateway_srv.utils;

import com.example.j2n.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class JwtGeneralUtil {
    private final JwtUtil jwtUtil;

    @Value("${internal.token}")
    private String internalToken;

    public JwtGeneralUtil(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.clock-skew-seconds:60}") long clockSkewSeconds) {
        this.jwtUtil = new JwtUtil(jwtSecret, clockSkewSeconds);
    }

    public Claims verify(String token) {
        return this.jwtUtil.validateToken(token);
    }
}