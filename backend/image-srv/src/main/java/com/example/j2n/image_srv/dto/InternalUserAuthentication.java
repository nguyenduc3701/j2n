package com.example.j2n.image_srv.dto;

import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import lombok.Getter;

@Getter
public class InternalUserAuthentication extends AbstractAuthenticationToken {

    private final String userId;
    private final String userName;
    private final String roleId;

    public InternalUserAuthentication(String userId, String userName, String roleId) {
        super(Collections.emptyList());
        this.userId = userId;
        this.userName = userName;
        this.roleId = roleId;
        setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
