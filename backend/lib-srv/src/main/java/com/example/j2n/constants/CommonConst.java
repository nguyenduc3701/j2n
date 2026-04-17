package com.example.j2n.constants;

public class CommonConst {
    public static final String X_INTERNAL_TOKEN = "X-Internal-Token";
    public static final String X_USER_ID = "X-User-Id";
    public static final String X_USER_NAME = "X-User-Name";
    public static final String X_ROLE_ID = "X-Role-Id";

    public static final Long ROLE_VISITOR_ID = 4L;
    public static final Long ROLE_RENTER_ID = 3L;
    public static final Long ROLE_RECRUITER_ID = 2L;
    public static final Long ROLE_ADMIN_ID = 1L;
    public static final int PASSWORD_MIN_LENGTH = 8;

    // Redis key prefix
    public static final String AUTH_SESSION_PREFIX = "auth:session:";
    public static final String AUTH_REFRESH_PREFIX = "auth:refresh:";
    public static final String AUTH_USER_SESSIONS_PREFIX = "auth:user_sessions:";

    // Cache key
    public static final String DASHBOARD_CACHE_KEY = "dashboardCache";
    public static final String CATEGORY_CACHE_KEY = "categoryCache";

    public static final String MAIN_REPORT_KEY = "'main_report'";
    public static final String ALL_CATEGORIES_KEY = "'all_categories'";
}
