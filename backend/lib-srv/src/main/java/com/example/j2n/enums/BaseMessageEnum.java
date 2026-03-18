package com.example.j2n.enums;

import lombok.Getter;

import com.example.j2n.dto.SimpleBaseMessage;
import com.example.j2n.impl.BaseMessage;

@Getter
public enum BaseMessageEnum implements BaseMessage {
    // ===== Success (200xxx) =====
    SUCCESS("200200", HttpStatusCode.OK, BaseMessageConstants.SUCCESS),

    // ===== System / Server Layer (500xxx) =====
    NON_RETRYABLE("400403", HttpStatusCode.BAD_REQUEST, BaseMessageConstants.NON_RETRYABLE),
    UNKNOWN_FIELDS("400401", HttpStatusCode.BAD_REQUEST, BaseMessageConstants.UNKNOWN_FIELDS),
    INTERNAL_ERROR("500500", HttpStatusCode.INTERNAL_SERVER_ERROR, BaseMessageConstants.INTERNAL_ERROR),
    ACCESS_DENIED("500501", HttpStatusCode.FORBIDDEN, BaseMessageConstants.ACCESS_DENIED),
    INVALID_REQUEST("500502", HttpStatusCode.BAD_REQUEST, BaseMessageConstants.INVALID_REQUEST),
    FIELD_REQUIRED("500503", HttpStatusCode.BAD_REQUEST, BaseMessageConstants.FIELD_REQUIRED),
    NOT_FOUND("500504", HttpStatusCode.NOT_FOUND, BaseMessageConstants.NOT_FOUND),
    UNAUTHORIZED("500505", HttpStatusCode.UNAUTHORIZED, BaseMessageConstants.UNAUTHORIZED),
    TIMEOUT("500506", HttpStatusCode.INTERNAL_SERVER_ERROR, BaseMessageConstants.TIMEOUT),
    EXTERNAL_SERVICE_ERROR("500507", HttpStatusCode.INTERNAL_SERVER_ERROR, BaseMessageConstants.EXTERNAL_SERVICE_ERROR),
    CONFIGURATION_ERROR("500508", HttpStatusCode.INTERNAL_SERVER_ERROR, BaseMessageConstants.CONFIGURATION_ERROR),
    BAD_REQUEST("500509", HttpStatusCode.BAD_REQUEST, BaseMessageConstants.BAD_REQUEST),
    SERIALIZATION_ERROR("500510", HttpStatusCode.INTERNAL_SERVER_ERROR, BaseMessageConstants.SERIALIZATION_ERROR);

    private final String code;
    private final HttpStatusCode httpStatus;
    private final String message;

    BaseMessageEnum(String code, HttpStatusCode httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public BaseMessage withArgs(Object... args) {
        return new SimpleBaseMessage(this.code, this.httpStatus, String.format(this.message, args));
    }

    public static class BaseMessageConstants {
        public static final String SUCCESS = "Success";
        public static final String NON_RETRYABLE = "Request is invalid and cannot be retried";
        public static final String UNKNOWN_FIELDS = "Unknown field";
        public static final String INTERNAL_ERROR = "Internal server error";
        public static final String ACCESS_DENIED = "Access denied";
        public static final String INVALID_REQUEST = "Invalid request";
        public static final String FIELD_REQUIRED = "Field %s is required";
        public static final String NOT_FOUND = "%s not found";
        public static final String UNAUTHORIZED = "Unauthorized";
        public static final String TIMEOUT = "Timeout";
        public static final String EXTERNAL_SERVICE_ERROR = "External service error";
        public static final String CONFIGURATION_ERROR = "Configuration error";
        public static final String BAD_REQUEST = "Bad request";
        public static final String SERIALIZATION_ERROR = "Serialization error";
    }
}
