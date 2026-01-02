package com.example.j2n.bff_srv.constant;

import lombok.Getter;

@Getter
public class GatewayPath {
    public static final String AUTH_LOGIN_PATH = "/api/auth/login";
    public static final String AUTH_REGISTER_PATH = "/api/auth/register";
    public static final String AUTH_GET_LIST_USERS_PATH = "/api/auth/users/list";
    public static final String AUTH_ME_PATH = "/api/auth/users/me";
    public static final String AUTH_USER_ID_PATH = "/api/auth/users/%s";
    public static final String AUTH_CREATE_USER_PATH = "/api/auth/users";
    public static final String AUTH_UPDATE_USER_PATH = "/api/auth/users/%s";
    public static final String AUTH_DELETE_USER_PATH = "/api/auth/users/%s";
    public static final String AUTH_GET_ROLES_PATH = "/api/auth/roles";
    public static final String AUTH_GET_PERMISSIONS_PATH = "/api/auth/permissions";
    public static final String AUTH_GET_PERMISSIONS_BY_ROLE_ID_PATH = "/api/auth/permissions/%s";
}
