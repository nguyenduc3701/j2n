package com.example.j2n.exception;

import com.example.j2n.impl.BaseMessage;

public class UnauthorizedException extends BaseServiceException {
    public UnauthorizedException(BaseMessage message) {
        super(message);
    }

    public UnauthorizedException(BaseMessage message, Throwable cause) {
        super(message, cause);
    }
}
