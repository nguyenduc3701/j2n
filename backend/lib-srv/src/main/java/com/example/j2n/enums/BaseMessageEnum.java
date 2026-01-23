package com.example.j2n.enums;

import lombok.Getter;

import com.example.j2n.dto.SimpleBaseMessage;
import com.example.j2n.impl.BaseMessage;

@Getter
public enum BaseMessageEnum implements BaseMessage {
    // ===== Success (200xxx) =====
    SUCCESS("200200", HttpStatusCode.OK, "Success"),

    // ===== System / Server Layer (500xxx) =====
    NON_RETRYABLE("400403", HttpStatusCode.BAD_REQUEST, "Request is invalid and cannot be retried"),
    UNKNOWN_FIELDS("400401", HttpStatusCode.BAD_REQUEST, "Unknown field"),
    INTERNAL_ERROR("500500", HttpStatusCode.INTERNAL_SERVER_ERROR, "Internal server error"),
    ACCESS_DENIED("500501", HttpStatusCode.FORBIDDEN, "Access denied"),
    INVALID_REQUEST("500502", HttpStatusCode.BAD_REQUEST, "Invalid request"),
    FIELD_REQUIRED("500503", HttpStatusCode.BAD_REQUEST, "Field %s is required"),
    NOT_FOUND("500504", HttpStatusCode.NOT_FOUND, "%s not found"),
    UNAUTHORIZED("500505", HttpStatusCode.UNAUTHORIZED, "Unauthorized"),
    TIMEOUT("500506", HttpStatusCode.INTERNAL_SERVER_ERROR, "Timeout"),
    EXTERNAL_SERVICE_ERROR("500507", HttpStatusCode.INTERNAL_SERVER_ERROR, "External service error"),
    CONFIGURATION_ERROR("500508", HttpStatusCode.INTERNAL_SERVER_ERROR, "Configuration error"),
    SERIALIZATION_ERROR("500509", HttpStatusCode.INTERNAL_SERVER_ERROR, "Serialization error");

    private final String code;
    private final HttpStatusCode httpStatus;
    private final String message;

    BaseMessageEnum(String code, HttpStatusCode httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public BaseMessage withArgs(Object... args) {
        return new SimpleBaseMessage(
                this.code,
                this.httpStatus,
                String.format(this.message, args));
    }
}
