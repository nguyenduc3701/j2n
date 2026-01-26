package com.example.j2n.auth_srv.utils.common;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CurrentUserTest {

    @InjectMocks
    private CurrentUser currentUser;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getId_Success() {
        when(request.getHeader("X-User-Id")).thenReturn("123");
        assertEquals("123", currentUser.getId());
    }

    @Test
    void getUserName_Success() {
        when(request.getHeader("X-User-Name")).thenReturn("testuser");
        assertEquals("testuser", currentUser.getUserName());
    }

    @Test
    void getRoleId_Success() {
        when(request.getHeader("X-Role-Id")).thenReturn("ADMIN");
        assertEquals("ADMIN", currentUser.getRoleId());
    }

    @Test
    void isFromBff_Success() {
        when(request.getHeader("FROM-BFF")).thenReturn("true");
        assertTrue(currentUser.isFromBff());
    }

    @Test
    void isFromBff_False() {
        when(request.getHeader("FROM-BFF")).thenReturn("false");
        assertFalse(currentUser.isFromBff());
    }

    @Test
    void isFromBff_Null() {
        when(request.getHeader("FROM-BFF")).thenReturn(null);
        assertFalse(currentUser.isFromBff());
    }

    @Test
    void isFromBff_CaseInsensitive_True() {
        when(request.getHeader("FROM-BFF")).thenReturn("TRUE");
        assertTrue(currentUser.isFromBff());
    }

    @Test
    void isFromBff_CaseInsensitive_True2() {
        when(request.getHeader("FROM-BFF")).thenReturn("True");
        assertTrue(currentUser.isFromBff());
    }

    @Test
    void getRequest_NoAttributes_ThrowsException() {
        RequestContextHolder.resetRequestAttributes();
        assertThrows(IllegalStateException.class, () -> currentUser.getId());
    }

    @Test
    void getRequest_NoAttributes_ThrowsException_ForGetUserName() {
        RequestContextHolder.resetRequestAttributes();
        assertThrows(IllegalStateException.class, () -> currentUser.getUserName());
    }

    @Test
    void getRequest_NoAttributes_ThrowsException_ForGetRoleId() {
        RequestContextHolder.resetRequestAttributes();
        assertThrows(IllegalStateException.class, () -> currentUser.getRoleId());
    }

    @Test
    void getRequest_NoAttributes_ThrowsException_ForIsFromBff() {
        RequestContextHolder.resetRequestAttributes();
        assertThrows(IllegalStateException.class, () -> currentUser.isFromBff());
    }
}
