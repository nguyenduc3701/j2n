package com.example.j2n.api_gateway_srv.utils;

import com.example.j2n.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtGeneralUtil {
    private final JwtUtil jwtUtil;

    public JwtGeneralUtil(@Value("${jwt.secret}") String jwtSecret,
                          @Value("${jwt.expiration-ms}") Long jwtExpirationMs) {
        this.jwtUtil = new JwtUtil(jwtSecret, jwtExpirationMs);
    }
    public Claims verify(String token) {
        return this.jwtUtil.validateToken(token);
    }
}