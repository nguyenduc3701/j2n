package com.example.j2n.travel_srv.dto;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class InternalUserAuthentication extends AbstractAuthenticationToken {

    private final String userId;
    private final String userName;
    private final String roleId;

    public InternalUserAuthentication(String userId, String userName, String roleId) {
        super(getAuthorities(roleId));
        this.userId = userId;
        this.userName = userName;
        this.roleId = roleId;
        setAuthenticated(true);
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(String roleId) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if ("1".equals(roleId)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authorities;
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
