package com.example.j2n.auth_srv.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtGeneralUtilTest {

    private JwtGeneralUtil jwtGeneralUtil;
    private final String secret = "9a4f4c3d801234567890abcdef1234567890abcdef1234567890abcdef123456";
    private final Long expiration = 3600000L;

    @BeforeEach
    void setUp() {
        jwtGeneralUtil = new JwtGeneralUtil(secret, 60L);
    }

    @Test
    void generateAndValidate_Success() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        String subject = "testuser";
        String jti = "test-jti";

        String token = jwtGeneralUtil.generate(claims, subject, jti, expiration);
        assertNotNull(token);

        Claims validatedClaims = jwtGeneralUtil.validate(token);
        assertEquals(subject, validatedClaims.getSubject());
        assertEquals("ADMIN", validatedClaims.get("role"));
        assertEquals(jti, validatedClaims.getId());
    }

    @Test
    void getSubjectAndSessionId_Success() {
        Map<String, Object> claims = new HashMap<>();
        String subject = "testuser";
        String sessionId = "test-session-id";

        String token = jwtGeneralUtil.generate(claims, subject, sessionId, expiration);

        assertEquals(subject, jwtGeneralUtil.getSubject(token));
        assertEquals(sessionId, jwtGeneralUtil.getSessionId(token));
    }
}
