package com.example.j2n.constants;

public class CommonConst {
    public static final Long ROLE_VISITOR_ID = 4L;
    public static final Long ROLE_RENTER_ID = 3L;
    public static final Long ROLE_RECRUITER_ID = 2L;
    public static final Long ROLE_ADMIN_ID = 1L;
    public static final int PASSWORD_MIN_LENGTH = 8;

    // Redis key prefix
    public static final String AUTH_SESSION_PREFIX = "auth:session:";
    public static final String AUTH_REFRESH_PREFIX = "auth:refresh:";
    public static final String AUTH_USER_SESSIONS_PREFIX = "auth:user_sessions:";
}
