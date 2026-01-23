package com.example.j2n.auth_srv.constant;

import com.example.j2n.dto.SimpleBaseMessage;
import com.example.j2n.enums.HttpStatusCode;
import lombok.Getter;
import com.example.j2n.impl.BaseMessage;

@Getter
public enum MessageEnum implements BaseMessage {
    CREATE_USER_SUCCESS("200201", HttpStatusCode.OK, "User created successfully"),
    UPDATE_USER_SUCCESS("200201", HttpStatusCode.OK, "User updated successfully"),
    DELETE_USER_SUCCESS("200201", HttpStatusCode.OK, "User deleted successfully"),

    ROLE_NOT_ALLOW_ACTION("400401", HttpStatusCode.BAD_REQUEST, "Role not allow action"),
    ROLE_NOT_ALLOW_CREATE_USER("400401", HttpStatusCode.BAD_REQUEST, "Role not allow create user"),
    ROLE_NOT_FOUND("400401", HttpStatusCode.BAD_REQUEST, "Role not found"),
    USER_NOT_FOUND("400401", HttpStatusCode.BAD_REQUEST, "User not found"),
    INVALID_CREDENTIALS("400401", HttpStatusCode.BAD_REQUEST, "Invalid credentials"),
    PASSWORD_TOO_SHORT("400401", HttpStatusCode.BAD_REQUEST, "Password too short"),
    FIELD_REQUIRED("400401", HttpStatusCode.BAD_REQUEST, "Field %s is required"),
    FIELD_EXISTED("400402", HttpStatusCode.BAD_REQUEST, "%s is already existed"),

    TOKEN_INVALID("300101", HttpStatusCode.UNAUTHORIZED, "Invalid token"),
    TOKEN_EXPIRED("300102", HttpStatusCode.UNAUTHORIZED, "Token expired"),
    INVALID_REFRESH_TOKEN("300103", HttpStatusCode.UNAUTHORIZED, "Invalid refresh token"),
    INVALID_REFRESH_TOKEN_EXPIRED("300104", HttpStatusCode.UNAUTHORIZED, "Invalid refresh token expired"),

    SERVICE_NOT_RECOGNIZED("500511", HttpStatusCode.INTERNAL_SERVER_ERROR, "Service not recognized");

    private final String code;
    private final HttpStatusCode httpStatus;
    private final String message;

    MessageEnum(String code, HttpStatusCode httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public BaseMessage withArgs(Object... args) {
        return new SimpleBaseMessage(
                this.code,
                this.httpStatus,
                String.format(this.message, args));
    }
}