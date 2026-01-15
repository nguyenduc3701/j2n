package com.example.j2n.auth_srv.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PasswordUtilTest {

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordUtil passwordUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void encode_Success() {
        String raw = "password";
        String encoded = "encoded";
        when(passwordEncoder.encode(raw)).thenReturn(encoded);

        String result = passwordUtil.encode(raw);

        assertEquals(encoded, result);
        verify(passwordEncoder).encode(raw);
    }

    @Test
    void matches_Success() {
        String raw = "password";
        String encoded = "encoded";
        when(passwordEncoder.matches(raw, encoded)).thenReturn(true);

        boolean result = passwordUtil.matches(raw, encoded);

        assertTrue(result);
        verify(passwordEncoder).matches(raw, encoded);
    }
}
