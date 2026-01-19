package com.example.j2n.exception;

import com.example.j2n.impl.BaseMessage;

public abstract class BaseServiceException extends RuntimeException {
    private final BaseMessage message;

    protected BaseServiceException(BaseMessage message) {
        super(message.getMessage());
        this.message = message;
    }

    protected BaseServiceException(BaseMessage message, Throwable cause) {
        super(message.getMessage(), cause);
        this.message = message;
    }

    public BaseMessage getBaseMessage() {
        return message;
    }

}