package com.example.j2n.auth_srv.utils.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Component
@RequestScope
public class CurrentUser {
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new IllegalStateException("No current request attributes found");
        }
        return attrs.getRequest();
    }

    public String getId() {
        return getRequest().getHeader("X-User-Id");
    }

    public String getUserName() {
        return getRequest().getHeader("X-User-Name");
    }

    public String getRoleId() {
        return getRequest().getHeader("X-Role-Id");
    }

    public boolean isFromBff() {
        String fromBff = getRequest().getHeader("FROM-BFF");
        return "true".equalsIgnoreCase(fromBff);
    }
}
