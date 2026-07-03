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
    public static final String ORDER_BASE_PATH = "/api/order/orders";
    public static final String ORDER_BY_USER_ID_PATH = "/api/order/orders/%s";
    // room-srv
    public static final String ROOM_SEARCH_PATH = "/api/room/search";
    public static final String ROOM_BASE_PATH = "/api/room/";
    public static final String ROOM_DETAIL_PATH = "/api/room/%s";
    public static final String ROOM_UPDATE_PATH = "/api/room/%s";
    public static final String ROOM_DELETE_PATH = "/api/room/%s";
    public static final String ROOM_UPDATE_FEES_PATH = "/api/room/%s/fees";
    public static final String ROOM_UPDATE_ASSETS_PATH = "/api/room/%s/assets";
    
    public static final String ROOM_ASSET_SEARCH_PATH = "/api/room/assets/search";
    public static final String ROOM_ASSET_BASE_PATH = "/api/room/assets";
    public static final String ROOM_ASSET_ID_PATH = "/api/room/assets/%s";

    public static final String ROOM_BILL_SEARCH_PATH = "/api/room/bills/search";
    public static final String ROOM_BILL_ADMIN_SEARCH_PATH = "/api/room/bills/admin/search";
    public static final String ROOM_BILL_CALCULATE_PATH = "/api/room/bills/calculate";
    public static final String ROOM_BILL_CALCULATE_ALL_PATH = "/api/room/bills/calculate-all";
    public static final String ROOM_BILL_BY_ROOM_ID_PATH = "/api/room/bills/room/%s";
    public static final String ROOM_BILL_PAY_PATH = "/api/room/bills/%s/pay";

    public static final String ROOM_FEE_BASE_PATH = "/api/room/fees";
    public static final String ROOM_FEE_ID_PATH = "/api/room/fees/%s";

    public static final String ROOM_MEMBER_BY_ROOM_ID_PATH = "/api/room/members/room/%s";
    public static final String ROOM_MEMBER_MAP_ROOM_PATH = "/api/room/members/mapping";
    public static final String ROOM_MEMBER_ID_PATH = "/api/room/members/%s";
    public static final String ROOM_MEMBER_BY_USER_ID_PATH = "/api/room/members/user/%s";

    // notification-srv
    public static final String NOTIFICATION_BASE_PATH = "/api/notification/notifications";
    public static final String NOTIFICATION_READ_PATH = "/api/notification/notifications/%s/read";
    public static final String NOTIFICATION_READ_ALL_PATH = "/api/notification/notifications/read-all";
    public static final String NOTIFICATION_EMAIL_SEND_PATH = "/api/notification/emails/send";
}
