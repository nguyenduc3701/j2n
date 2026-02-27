package com.example.j2n.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

public class JwtUtil {

    private final Key signingKey;

    @Value("${application.jwt.clock-skew-seconds:60}")
    private long clockSkewSeconds;

    public JwtUtil(String secret) {
        this(secret, 60); // Default 60 seconds clock skew
    }

    public JwtUtil(String secret, long clockSkewSeconds) {
        this.signingKey = Keys.hmacShaKeyFor(Base64.getUrlDecoder().decode(secret.trim()));
        this.clockSkewSeconds = clockSkewSeconds;
    }

    public String generateToken(Map<String, Object> claims, String subject, String sessionId, long expirationMs) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setId(sessionId)
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .setAllowedClockSkewSeconds(clockSkewSeconds)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getSubject(String token) {
        return validateToken(token).getSubject();
    }

    public String getSessionId(String token) {
        return validateToken(token).getId();
    }
}
