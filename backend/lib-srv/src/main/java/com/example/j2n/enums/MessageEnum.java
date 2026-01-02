package com.example.j2n.enums;

import lombok.Getter;

@Getter
public enum MessageEnum {
    // ===== Success =====
    SUCCESS("500001", HttpStatusCode.OK, "Success"),
    DELETE_USER_SUCCESS("500002", HttpStatusCode.OK, "Delete user successfully"),
    UPDATE_USER_SUCCESS("500003", HttpStatusCode.OK, "Update user successfully"),
    CREATE_USER_SUCCESS("500004", HttpStatusCode.OK, "Create user successfully"),

    // ===== User / Validation Layer (100xxx) =====
    FIELD_REQUIRED("100001", HttpStatusCode.BAD_REQUEST, "Field [%s] is required"),
    UNKNOWN_FIELDS("100002", HttpStatusCode.BAD_REQUEST, "Unknown fields [%s] in request"),
    USERNAME_ALREADY_EXISTS("100101", HttpStatusCode.CONFLICT, "Username already exists"),
    EMAIL_ALREADY_EXISTS("100102", HttpStatusCode.CONFLICT, "Email already exists"),
    PASSWORD_TOO_SHORT("100103", HttpStatusCode.BAD_REQUEST, "Password too short"),
    INVALID_USER_INFORMATION("100104", HttpStatusCode.BAD_REQUEST, "Invalid user information"),
    USER_NOT_ALLOW_ACTION("100105", HttpStatusCode.BAD_REQUEST, "User not allow action"),
    INVALID_CREDENTIALS("100106", HttpStatusCode.BAD_REQUEST, "Invalid username or password"),
    PASSWORD_INVALID("100107", HttpStatusCode.NOT_FOUND, "Password invalid"),
    USER_NOT_FOUND("100108", HttpStatusCode.NOT_FOUND, "User not found"),

    // ===== Role / Permission Layer (200xxx) =====
    ROLE_NOT_FOUND("200101", HttpStatusCode.NOT_FOUND, "Role not found"),
    ROLE_NOT_ALLOW_ACTION("200102", HttpStatusCode.BAD_REQUEST, "Role not allow action"),
    ROLE_NOT_ALLOW_CREATE_USER("200103", HttpStatusCode.BAD_REQUEST, "Role not allow create user"),
    PERMISSION_DENIED("200104", HttpStatusCode.FORBIDDEN, "Permission denied"),
    ACCESS_DENIED("200105", HttpStatusCode.FORBIDDEN, "Access denied"),

    // ===== Token / Auth Layer (300xxx) =====
    TOKEN_INVALID("300101", HttpStatusCode.UNAUTHORIZED, "Invalid token"),
    TOKEN_EXPIRED("300102", HttpStatusCode.UNAUTHORIZED, "Token expired"),
    REFRESH_TOKEN_EXPIRED("300103", HttpStatusCode.UNAUTHORIZED, "Refresh token expired"),

    // ===== System / Server Layer (500xxx) =====
    INTERNAL_ERROR("500500", HttpStatusCode.INTERNAL_SERVER_ERROR, "Internal server error"),
    DATABASE_CONNECTION_ERROR("500501", HttpStatusCode.INTERNAL_SERVER_ERROR, "Database connection error"),
    SERVICE_UNAVAILABLE("500502", HttpStatusCode.INTERNAL_SERVER_ERROR, "Service unavailable"),
    SERVICE_NOT_RECOGNIZED("500503", HttpStatusCode.INTERNAL_SERVER_ERROR, "Service not recognized"),
    TIMEOUT_ERROR("500504", HttpStatusCode.INTERNAL_SERVER_ERROR, "Request timeout");

    private final String code;
    private final HttpStatusCode httpStatus;
    private final String message;

    MessageEnum(String code, HttpStatusCode httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
