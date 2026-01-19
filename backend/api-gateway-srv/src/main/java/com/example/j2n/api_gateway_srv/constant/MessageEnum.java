
package com.example.j2n.api_gateway_srv.constant;

import com.example.j2n.dto.SimpleBaseMessage;
import com.example.j2n.enums.HttpStatusCode;
import com.example.j2n.impl.BaseMessage;
import lombok.Getter;

@Getter
public enum MessageEnum implements BaseMessage {
    // ===== Token / Auth Layer (300xxx) =====
    TOKEN_INVALID("300101", HttpStatusCode.UNAUTHORIZED, "Invalid token"),
    TOKEN_EXPIRED("300102", HttpStatusCode.UNAUTHORIZED, "Token expired"),

    // ===== System / Server Layer (500xxx) =====
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
