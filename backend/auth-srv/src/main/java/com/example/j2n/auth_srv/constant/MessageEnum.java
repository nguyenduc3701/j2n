package com.example.j2n.auth_srv.constant;

import lombok.Getter;

@Getter
public enum MessageEnum {
    SUCCESS("200", "Success"),
    USERNAME_ALREADY_EXISTS("409", "Username already exists"),
    USER_NOT_FOUND("404", "User not found"),
    PASSWORD_INVALID("404", "Password invalid"),
    INVALID_CREDENTIALS("400", "Invalid username or password"),
    TOKEN_INVALID("401", "Invalid token"),
    TOKEN_EXPIRED("401", "Token expired"),
    ACCESS_DENIED("403", "Access denied"),
    FIELD_REQUIRED("400", "Field [%s] is required"),
    ROLE_NOT_FOUND("404", "Role not found"),
    INTERNAL_ERROR("500", "Internal server error");

    private final String code;
    private final String message;

    MessageEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
