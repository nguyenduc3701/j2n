package com.example.j2n.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private final Key signingKey;
    private final long expirationMs;

    // Nhận secret + expiration từ service khi khởi tạo
    public JwtUtil(String secret, long expirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(Base64.getUrlDecoder().decode(secret));
        this.expirationMs = expirationMs;
    }

    // Tạo token
    public String generateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Verify token
    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Lấy subject từ token
    public String getSubject(String token) {
        return validateToken(token).getSubject();
    }
}
