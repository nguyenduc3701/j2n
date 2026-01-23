package com.example.j2n.api_gateway_srv.utils;

import io.jsonwebtoken.Claims;
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

    @BeforeEach
    void setUp() {
        jwtGeneralUtil = new JwtGeneralUtil(secret, 60L);
        ReflectionTestUtils.setField(jwtGeneralUtil, "internalToken", "test-internal-token");
    }

    @Test
    void verify_Success() {
        // We need a valid token to test verify.
        // Since JwtGeneralUtil uses JwtUtil internally, we can use a temporary JwtUtil
        // to generate one.
        com.example.j2n.utils.JwtUtil tempUtil = new com.example.j2n.utils.JwtUtil(secret, 60L);
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", "123");
        String token = tempUtil.generateToken(claims, "testuser", "jti", 3600000L);

        Claims validatedClaims = jwtGeneralUtil.verify(token);
        assertNotNull(validatedClaims);
        assertEquals("testuser", validatedClaims.getSubject());
        assertEquals("123", validatedClaims.get("user_id"));
    }

    @Test
    void getInternalToken_Success() {
        assertEquals("test-internal-token", jwtGeneralUtil.getInternalToken());
    }
}
