package com.example.j2n.bff_srv.constant;

import com.example.j2n.dto.SimpleBaseMessage;
import com.example.j2n.enums.HttpStatusCode;
import com.example.j2n.impl.BaseMessage;

import lombok.Getter;

@Getter
public enum MessageEnum implements BaseMessage {
    SUCCESS("200", HttpStatusCode.OK, MessageConstants.SUCCESS),
    FILE_NOT_FOUND("404", HttpStatusCode.NOT_FOUND, MessageConstants.FILE_NOT_FOUND),
    GATEWAY_REQUEST_FAILED("500", HttpStatusCode.INTERNAL_SERVER_ERROR, MessageConstants.GATEWAY_REQUEST_FAILED),
    INTERNAL_ERROR("500", HttpStatusCode.INTERNAL_SERVER_ERROR, MessageConstants.INTERNAL_ERROR);

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

    public static class MessageConstants {
        public static final String SUCCESS = "Success";
        public static final String FILE_NOT_FOUND = "File not found";
        public static final String GATEWAY_REQUEST_FAILED = "Request to gateway failed";
        public static final String INTERNAL_ERROR = "Internal server error";
    }
}
