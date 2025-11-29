package com.example.j2n.bff_srv.constant;

import lombok.Getter;

@Getter
public class GatewayPath {
    public static final String AUTH_LOGIN = "/api/auth/login";
    public static final String AUTH_REGISTER = "/api/auth/register";
    public static final String AUTH_USERS = "/api/auth/users";
}
