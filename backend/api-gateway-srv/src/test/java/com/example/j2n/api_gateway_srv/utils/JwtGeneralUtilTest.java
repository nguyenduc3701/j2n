package com.example.j2n.api_gateway_srv.utils;

import com.example.j2n.utils.JwtUtil;
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
    // Secret phải là Base64 URL encoded hợp lệ để phù hợp với Base64.getUrlDecoder() trong JwtUtil
    private final String secret = "OWE0ZjRjM2Q4MDEyMzQ1Njc4OTBhYmNkZWYxMjM0NTY3ODkwYWJjZGVmMTIzNDU2Nzg5MGFiY2RlZjEyMzQ1Ng==";

    @BeforeEach
    void setUp() {
        jwtGeneralUtil = new JwtGeneralUtil(secret, 60L);
        ReflectionTestUtils.setField(jwtGeneralUtil, "internalToken", "test-internal-token");
    }

    @Test
    void verify_Success() {
        // Test verify và đồng thời test luôn việc JwtUtil được khởi tạo đúng
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", "123");

        // Sử dụng chính jwtUtil bên trong để tạo token nhằm test getter getJwtUtil()
        String token = jwtGeneralUtil.getJwtUtil().generateToken(claims, "testuser", "jti", 3600000L);

        Claims validatedClaims = jwtGeneralUtil.verify(token);

        assertNotNull(validatedClaims);
        assertEquals("testuser", validatedClaims.getSubject());
        assertEquals("123", validatedClaims.get("user_id"));
    }

    @Test
    void getInternalToken_Success() {
        assertEquals("test-internal-token", jwtGeneralUtil.getInternalToken());
    }

    @Test
    void testJwtUtil_ConstructorWithDefaultClockSkew() {
        // Case này để cover constructor 1 tham số của JwtUtil: public JwtUtil(String secret)
        JwtUtil util = new JwtUtil(secret);
        assertNotNull(util);

        // Verify thử một token với instance này để đảm bảo nó hoạt động
        String token = util.generateToken(new HashMap<>(), "sub", "id", 1000L);
        assertEquals("sub", util.getSubject(token));
        assertEquals("id", util.getSessionId(token));
    }

    @Test
    void testJwtUtil_Getters() {
        // Cover nốt các method nhỏ trong JwtUtil nếu công cụ đo coverage yêu cầu
        JwtUtil util = jwtGeneralUtil.getJwtUtil();
        String token = util.generateToken(new HashMap<>(), "sub", "id", 1000L);

        assertNotNull(util.getSubject(token));
        assertNotNull(util.getSessionId(token));
    }
}