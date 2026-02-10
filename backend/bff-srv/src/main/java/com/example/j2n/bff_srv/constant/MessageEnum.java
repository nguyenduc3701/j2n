package com.example.j2n.bff_srv.constant;

import com.example.j2n.dto.SimpleBaseMessage;
import com.example.j2n.enums.HttpStatusCode;
import com.example.j2n.impl.BaseMessage;

import lombok.Getter;

@Getter
public enum MessageEnum implements BaseMessage {
    SUCCESS("200", HttpStatusCode.OK, "Success"),
    FILE_NOT_FOUND("404", HttpStatusCode.NOT_FOUND, "File not found"),
    GATEWAY_REQUEST_FAILED("500", HttpStatusCode.INTERNAL_SERVER_ERROR, "Request to gateway failed"),
    INTERNAL_ERROR("500", HttpStatusCode.INTERNAL_SERVER_ERROR, "Internal server error");

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
