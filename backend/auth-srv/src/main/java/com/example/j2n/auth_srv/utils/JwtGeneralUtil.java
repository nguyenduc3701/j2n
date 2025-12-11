package com.example.j2n.auth_srv.utils;

import com.example.j2n.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JwtGeneralUtil {
    private final JwtUtil jwtUtil;

    public JwtGeneralUtil(@Value("${jwt.secret}") String jwtSecret,
                          @Value("${jwt.expiration-ms}") Long jwtExpirationMs) {
        this.jwtUtil = new JwtUtil(jwtSecret, jwtExpirationMs);
    }
    public Claims validate(String token) {
        return this.jwtUtil.validateToken(token);
    }
    public String generate(Map<String, Object> claims, String subject) {
        return this.jwtUtil.generateToken(claims,subject);
    }

}