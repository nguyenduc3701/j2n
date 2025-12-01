package com.example.j2n.auth_srv.utils.common;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CurrentUser {
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new IllegalStateException("No current request attributes found");
        }
        return attrs.getRequest();
    }

    public String getCurrentUsername() {
        return getRequest().getHeader("X-User-Name");
    }

    public String getCurrentUserId() {
        return getRequest().getHeader("X-User-Id");
    }

    public String getCurrentRoleId() {
        return getRequest().getHeader("X-Role-Id");
    }

    public boolean isFromBff() {
        String fromBff = getRequest().getHeader("FROM-BFF");
        return "true".equalsIgnoreCase(fromBff);
    }
}
