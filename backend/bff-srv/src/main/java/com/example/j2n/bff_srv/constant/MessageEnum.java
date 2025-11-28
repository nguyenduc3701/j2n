package com.example.j2n.bff_srv.constant;

import lombok.Getter;

@Getter
public enum MessageEnum {
    SUCCESS("200", "Success"),
    GATEWAY_REQUEST_FAILED("500", "Request [%s] to gateway failed"),
    INTERNAL_ERROR("500", "Internal server error");

    private final String code;
    private final String message;

    MessageEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
