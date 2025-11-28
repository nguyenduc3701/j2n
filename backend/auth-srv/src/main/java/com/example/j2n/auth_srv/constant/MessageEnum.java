package com.example.j2n.auth_srv.constant;

import lombok.Getter;

@Getter
public enum MessageEnum {
    SUCCESS("AUTH_200_000", "Success"),
    USERNAME_ALREADY_EXISTS("AUTH_409_001", "Username already exists"),
    USER_NOT_FOUND("AUTH_404_001", "User not found"),
    PASSWORD_INVALID("AUTH_404_002", "Password invalid"),
    INVALID_CREDENTIALS("AUTH_400_001", "Invalid username or password"),
    TOKEN_INVALID("AUTH_401_001", "Invalid token"),
    TOKEN_EXPIRED("AUTH_401_002", "Token expired"),
    ACCESS_DENIED("AUTH_403_001", "Access denied"),
    FIELD_REQUIRED("AUTH_400_002", "Field [%s] is required"),
    INTERNAL_ERROR("AUTH_500_001", "Internal server error");

    private final String code;
    private final String message;

    MessageEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
