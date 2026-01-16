package com.example.j2n.enums;

import lombok.Getter;
import com.example.j2n.impl.BaseMessage;

@Getter
public enum BaseMessageEnum implements BaseMessage {
    // ===== Success (200xxx) =====
    SUCCESS("200200", HttpStatusCode.OK, "Success"),

    // ===== System / Server Layer (500xxx) =====
    INTERNAL_ERROR("500500", HttpStatusCode.INTERNAL_SERVER_ERROR, "Internal server error"),
    ACCESS_DENIED("500501", HttpStatusCode.FORBIDDEN, "Access denied"),
    INVALID_REQUEST("500502", HttpStatusCode.BAD_REQUEST, "Invalid request"),
    NOT_FOUND("500503", HttpStatusCode.NOT_FOUND, "Not found"),
    UNAUTHORIZED("500504", HttpStatusCode.UNAUTHORIZED, "Unauthorized"),
    TIMEOUT("500505", HttpStatusCode.INTERNAL_SERVER_ERROR, "Timeout"),
    EXTERNAL_SERVICE_ERROR("500506", HttpStatusCode.INTERNAL_SERVER_ERROR, "External service error"),
    CONFIGURATION_ERROR("500507", HttpStatusCode.INTERNAL_SERVER_ERROR, "Configuration error"),
    SERIALIZATION_ERROR("500508", HttpStatusCode.INTERNAL_SERVER_ERROR, "Serialization error");

    private final String code;
    private final HttpStatusCode httpStatus;
    private final String message;

    BaseMessageEnum(String code, HttpStatusCode httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
