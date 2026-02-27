package com.example.j2n.config_srv.constant;

import com.example.j2n.dto.SimpleBaseMessage;
import com.example.j2n.enums.HttpStatusCode;
import lombok.Getter;
import com.example.j2n.impl.BaseMessage;

@Getter
public enum MessageEnum implements BaseMessage {
    SERVICE_NOT_FOUND("400401", HttpStatusCode.BAD_REQUEST, "Service not found"),
    INVALID_REQUEST("400401", HttpStatusCode.BAD_REQUEST, "Invalid request"),
    CONFIGURATION_NOT_FOUND("400401", HttpStatusCode.BAD_REQUEST, "Configuration not found"),
    CONFIGURATION_NOT_UPDATABLE("400401", HttpStatusCode.BAD_REQUEST, "This configuration cannot be updated");

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