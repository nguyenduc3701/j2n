package com.example.j2n.auth_srv.constant;

import com.example.j2n.dto.SimpleBaseMessage;
import com.example.j2n.enums.HttpStatusCode;
import lombok.Getter;
import com.example.j2n.impl.BaseMessage;

@Getter
public enum MessageEnum implements BaseMessage {
    CREATE_USER_SUCCESS("200201", HttpStatusCode.OK, MessageConstants.CREATE_USER_SUCCESS),
    UPDATE_USER_SUCCESS("200201", HttpStatusCode.OK, MessageConstants.UPDATE_USER_SUCCESS),
    DELETE_USER_SUCCESS("200201", HttpStatusCode.OK, MessageConstants.DELETE_USER_SUCCESS),

    ROLE_NOT_ALLOW_ACTION("400401", HttpStatusCode.BAD_REQUEST, MessageConstants.ROLE_NOT_ALLOW_ACTION),
    ROLE_NOT_ALLOW_CREATE_USER("400401", HttpStatusCode.BAD_REQUEST, MessageConstants.ROLE_NOT_ALLOW_CREATE_USER),
    ROLE_NOT_FOUND("400401", HttpStatusCode.BAD_REQUEST, MessageConstants.ROLE_NOT_FOUND),
    USER_NOT_FOUND("400401", HttpStatusCode.BAD_REQUEST, MessageConstants.USER_NOT_FOUND),
    INVALID_CREDENTIALS("400401", HttpStatusCode.BAD_REQUEST, MessageConstants.INVALID_CREDENTIALS),
    PASSWORD_TOO_SHORT("400401", HttpStatusCode.BAD_REQUEST, MessageConstants.PASSWORD_TOO_SHORT),
    FIELD_REQUIRED("400401", HttpStatusCode.BAD_REQUEST, MessageConstants.FIELD_REQUIRED),
    FIELD_EXISTED("400402", HttpStatusCode.BAD_REQUEST, MessageConstants.FIELD_EXISTED),

    TOKEN_INVALID("300101", HttpStatusCode.UNAUTHORIZED, MessageConstants.TOKEN_INVALID),
    TOKEN_EXPIRED("300102", HttpStatusCode.UNAUTHORIZED, MessageConstants.TOKEN_EXPIRED),
    INVALID_REFRESH_TOKEN("300103", HttpStatusCode.UNAUTHORIZED, MessageConstants.INVALID_REFRESH_TOKEN),
    INVALID_REFRESH_TOKEN_EXPIRED("300104", HttpStatusCode.UNAUTHORIZED,
            MessageConstants.INVALID_REFRESH_TOKEN_EXPIRED),

    SERVICE_NOT_RECOGNIZED("500511", HttpStatusCode.INTERNAL_SERVER_ERROR, MessageConstants.SERVICE_NOT_RECOGNIZED),
    NOT_SET("000000", HttpStatusCode.OK, MessageConstants.NOT_SET);

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
        return new SimpleBaseMessage(this.code, this.httpStatus, String.format(this.message, args));
    }

    public static class MessageConstants {
        public static final String CREATE_USER_SUCCESS = "User created successfully";
        public static final String UPDATE_USER_SUCCESS = "User updated successfully";
        public static final String DELETE_USER_SUCCESS = "User deleted successfully";
        public static final String ROLE_NOT_ALLOW_ACTION = "Role not allow action";
        public static final String ROLE_NOT_ALLOW_CREATE_USER = "Role not allow create user";
        public static final String ROLE_NOT_FOUND = "Role not found";
        public static final String USER_NOT_FOUND = "User not found";
        public static final String INVALID_CREDENTIALS = "Invalid credentials";
        public static final String PASSWORD_TOO_SHORT = "Password too short";
        public static final String FIELD_REQUIRED = "Field %s is required";
        public static final String FIELD_EXISTED = "%s is already existed";
        public static final String TOKEN_INVALID = "Invalid token";
        public static final String TOKEN_EXPIRED = "Token expired";
        public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";
        public static final String INVALID_REFRESH_TOKEN_EXPIRED = "Invalid refresh token expired";
        public static final String SERVICE_NOT_RECOGNIZED = "Service not recognized";
        public static final String NOT_SET = "";
    }
}