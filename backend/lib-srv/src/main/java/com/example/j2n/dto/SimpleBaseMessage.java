package com.example.j2n.dto;

import com.example.j2n.enums.HttpStatusCode;
import com.example.j2n.impl.BaseMessage;

import lombok.Getter;

@Getter
public class SimpleBaseMessage implements BaseMessage {
    private final String code;
    private final HttpStatusCode httpStatus;
    private final String message;

    public SimpleBaseMessage(String code, HttpStatusCode httpStatus, String message) {
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
