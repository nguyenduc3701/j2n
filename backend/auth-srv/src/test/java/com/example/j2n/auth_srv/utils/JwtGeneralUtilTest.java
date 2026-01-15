package com.example.j2n.auth_srv.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

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
        jwtGeneralUtil = new JwtGeneralUtil(secret, expiration);
    }

    @Test
    void generateAndValidate_Success() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        String subject = "testuser";

        String token = jwtGeneralUtil.generate(claims, subject);
        assertNotNull(token);

        Claims validatedClaims = jwtGeneralUtil.validate(token);
        assertEquals(subject, validatedClaims.getSubject());
        assertEquals("ADMIN", validatedClaims.get("role"));
    }
}
