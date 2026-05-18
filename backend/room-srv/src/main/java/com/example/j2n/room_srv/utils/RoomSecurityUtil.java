package com.example.j2n.room_srv.utils;

import com.example.j2n.constants.CommonConst;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.exception.AccessDeniedException;
import com.example.j2n.room_srv.repository.entity.RoomEntity;
import com.example.j2n.security.InternalUserAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class RoomSecurityUtil {

    /**
     * Verifies if the current authenticated user has permission to access the room.
     * Access is granted if the user is an ADMIN or a member of the room.
     * Throws AccessDeniedException (HTTP 403 Forbidden) if unauthorized.
     *
     * @param room the room entity to check access for
     */
    public void checkRoomAccess(RoomEntity room) {
        if (room == null) {
            throw new AccessDeniedException(BaseMessageEnum.ACCESS_DENIED);
        }
        if (isAdmin()) {
            return; // Admin has full access
        }
        
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            boolean isMember = room.getMembers() != null && room.getMembers().stream()
                    .anyMatch(member -> member.getUserId() != null && member.getUserId().equals(currentUserId));
            if (isMember) {
                return; // Room member has access
            }
        }
        throw new AccessDeniedException(BaseMessageEnum.ACCESS_DENIED);
    }

    /**
     * Retrieves the current authenticated user's ID as a Long.
     *
     * @return current user's ID or null if unauthenticated
     */
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof InternalUserAuthentication) {
            String userIdStr = ((InternalUserAuthentication) auth).getUserId();
            if (userIdStr != null && !userIdStr.isBlank()) {
                return Long.valueOf(userIdStr);
            }
        }
        return null;
    }

    /**
     * Checks if the current authenticated user is an ADMIN.
     *
     * @return true if admin, false otherwise
     */
    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof InternalUserAuthentication) {
            return auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_" + CommonConst.ROLE_ADMIN_ID));
        }
        return false;
    }
}
