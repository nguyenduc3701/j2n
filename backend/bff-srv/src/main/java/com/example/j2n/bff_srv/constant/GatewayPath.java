package com.example.j2n.bff_srv.constant;

import lombok.Getter;

@Getter
public class GatewayPath {
    // auth-srv
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
    public static final String AUTH_REFRESH_TOKEN_PATH = "/api/auth/refresh-token";
    public static final String AUTH_LOGOUT_PATH = "/api/auth/logout";
    // image-srv
    public static final String IMAGE_UPLOAD_PATH = "/api/image/upload";
    public static final String IMAGE_GET_IMAGE_PATH = "/api/image/%s/%s";
    public static final String IMAGE_DOWNLOAD_FILE_PATH = "/api/image/file/curriculum-vitae/%s";
    // report-srv
    public static final String REPORT_MANAGEMENT_DASHBOARD_PATH = "/api/report/management/dashboard";
    // config-srv
    public static final String CONFIGURATION_BASE_PATH = "/api/centralize/configurations";
    public static final String CONFIGURATION_BY_KEY_PATH = "/api/centralize/configurations/%s";
    public static final String CONFIGURATION_BY_KEYS_PATH = "/api/centralize/configurations/keys";
    // payment-srv
    public static final String PAYMENT_TRANSACTIONS_PATH = "/api/payment/transactions";
    // order-srv
    public static final String ORDER_CARTS_PATH = "/api/order/carts";
}
