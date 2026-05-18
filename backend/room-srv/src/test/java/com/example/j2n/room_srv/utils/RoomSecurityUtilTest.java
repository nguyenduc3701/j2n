package com.example.j2n.room_srv.utils;

import com.example.j2n.constants.CommonConst;
import com.example.j2n.exception.AccessDeniedException;
import com.example.j2n.room_srv.repository.entity.RoomEntity;
import com.example.j2n.room_srv.repository.entity.RoomMemberEntity;
import com.example.j2n.security.InternalUserAuthentication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomSecurityUtilTest {

    private final RoomSecurityUtil roomSecurityUtil = new RoomSecurityUtil();

    @Mock
    private SecurityContext securityContext;

    @Mock
    private InternalUserAuthentication internalAuth;

    @Mock
    private Authentication genericAuth;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void checkRoomAccess_NullRoom_ThrowsAccessDeniedException() {
        assertThrows(AccessDeniedException.class, () -> roomSecurityUtil.checkRoomAccess(null));
    }

    @Test
    void checkRoomAccess_AdminUser_Success() {
        RoomEntity room = new RoomEntity();

        when(securityContext.getAuthentication()).thenReturn(internalAuth);
        when(internalAuth.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + CommonConst.ROLE_ADMIN_ID)));

        assertDoesNotThrow(() -> roomSecurityUtil.checkRoomAccess(room));
    }

    @Test
    void checkRoomAccess_RoomMemberUser_Success() {
        Long userId = 123L;
        RoomEntity room = RoomEntity.builder()
                .members(List.of(RoomMemberEntity.builder().userId(userId).build()))
                .build();

        when(securityContext.getAuthentication()).thenReturn(internalAuth);
        // Is not Admin
        when(internalAuth.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + CommonConst.ROLE_RENTER_ID)));
        // Is member
        when(internalAuth.getUserId()).thenReturn(userId.toString());

        assertDoesNotThrow(() -> roomSecurityUtil.checkRoomAccess(room));
    }

    @Test
    void checkRoomAccess_NotRoomMemberUser_ThrowsAccessDeniedException() {
        Long userId = 123L;
        RoomEntity room = RoomEntity.builder()
                .members(List.of(RoomMemberEntity.builder().userId(456L).build()))
                .build();

        when(securityContext.getAuthentication()).thenReturn(internalAuth);
        // Is not Admin
        when(internalAuth.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + CommonConst.ROLE_RENTER_ID)));
        // Is not member
        when(internalAuth.getUserId()).thenReturn(userId.toString());

        assertThrows(AccessDeniedException.class, () -> roomSecurityUtil.checkRoomAccess(room));
    }

    @Test
    void checkRoomAccess_GenericAuthentication_ThrowsAccessDeniedException() {
        RoomEntity room = new RoomEntity();

        when(securityContext.getAuthentication()).thenReturn(genericAuth);

        assertThrows(AccessDeniedException.class, () -> roomSecurityUtil.checkRoomAccess(room));
    }

    @Test
    void checkRoomAccess_NoAuthentication_ThrowsAccessDeniedException() {
        RoomEntity room = new RoomEntity();

        when(securityContext.getAuthentication()).thenReturn(null);

        assertThrows(AccessDeniedException.class, () -> roomSecurityUtil.checkRoomAccess(room));
    }

    @Test
    void getCurrentUserId_BlankUserId_ReturnsNull() {
        when(securityContext.getAuthentication()).thenReturn(internalAuth);
        when(internalAuth.getUserId()).thenReturn("");

        assertNull(roomSecurityUtil.getCurrentUserId());
    }

    @Test
    void getCurrentUserId_NullUserId_ReturnsNull() {
        when(securityContext.getAuthentication()).thenReturn(internalAuth);
        when(internalAuth.getUserId()).thenReturn(null);

        assertNull(roomSecurityUtil.getCurrentUserId());
    }
}
