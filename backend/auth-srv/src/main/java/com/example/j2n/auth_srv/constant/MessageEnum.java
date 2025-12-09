package com.example.j2n.auth_srv.constant;

import lombok.Getter;

@Getter
public enum MessageEnum {
    SUCCESS("200", "Success"),
    USERNAME_ALREADY_EXISTS("409", "Username already exists"),
    EMAIL_ALREADY_EXISTS("409", "Email already exists"),
    USER_NOT_FOUND("404", "User not found"),
    PASSWORD_INVALID("404", "Password invalid"),
    INVALID_CREDENTIALS("400", "Invalid username or password"),
    TOKEN_INVALID("401", "Invalid token"),
    TOKEN_EXPIRED("401", "Token expired"),
    ACCESS_DENIED("403", "Access denied"),
    FIELD_REQUIRED("400", "Field [%s] is required"),
    UNKNOWN_FIELDS("400", "Unknown fields [%s] in request"),
    ROLE_NOT_FOUND("404", "Role not found"),
    ROLE_NOT_ALLOW_ACTION("400", "Role not allow action"),
    ROLE_NOT_ALLOW_CREATE_USER("400", "Role not allow create user"),
    PERMISSION_DENIED("403", "Permission denied"),
    DELETE_USER_SUCCESS("200", "Delete user successfully"),
    USER_NOT_ALLOW_ACTION("400", "User not allow action"),
    PASSWORD_TOO_SHORT("400", "Password too short"),
    INVALID_USER_INFORMATION("400", "Invalid user information"),
    INTERNAL_ERROR("500", "Internal server error");

    private final String code;
    private final String message;

    MessageEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
