package com.example.j2n.auth_srv.constant;

import com.example.j2n.enums.HttpStatusCode;
import lombok.Getter;
import com.example.j2n.interfaces.BaseMessage;

@Getter
public enum MessageEnum implements BaseMessage {
    CREATE_USER_SUCCESS("200201", HttpStatusCode.OK, "User created successfully"),
    UPDATE_USER_SUCCESS("200201", HttpStatusCode.OK, "User updated successfully"),
    DELETE_USER_SUCCESS("200201", HttpStatusCode.OK, "User deleted successfully"),

    UNKNOWN_FIELDS("400401", HttpStatusCode.BAD_REQUEST, "Unknown fields"),
    ROLE_NOT_ALLOW_ACTION("400401", HttpStatusCode.BAD_REQUEST, "Role not allow action"),
    ROLE_NOT_ALLOW_CREATE_USER("400401", HttpStatusCode.BAD_REQUEST, "Role not allow create user"),
    ROLE_NOT_FOUND("400401", HttpStatusCode.BAD_REQUEST, "Role not found"),
    USER_NOT_FOUND("400401", HttpStatusCode.BAD_REQUEST, "User not found"),
    INVALID_CREDENTIALS("400401", HttpStatusCode.BAD_REQUEST, "Invalid credentials"),
    PASSWORD_TOO_SHORT("400401", HttpStatusCode.BAD_REQUEST, "Password too short"),
    USERNAME_ALREADY_EXISTS("400402", HttpStatusCode.BAD_REQUEST, "Username already exists"),
    EMAIL_ALREADY_EXISTS("400402", HttpStatusCode.BAD_REQUEST, "Email already exists"),
    FIELD_REQUIRED("400401", HttpStatusCode.BAD_REQUEST, "Field %s is required"),

    TOKEN_INVALID("300101", HttpStatusCode.UNAUTHORIZED, "Invalid token"),
    TOKEN_EXPIRED("300102", HttpStatusCode.UNAUTHORIZED, "Token expired"),

    SERVICE_NOT_RECOGNIZED("500511", HttpStatusCode.INTERNAL_SERVER_ERROR, "Service not recognized");

    private final String code;
    private final HttpStatusCode httpStatus;
    private final String message;

    MessageEnum(String code, HttpStatusCode httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}