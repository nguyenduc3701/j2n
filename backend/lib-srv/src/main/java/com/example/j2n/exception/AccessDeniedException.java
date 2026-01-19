package com.example.j2n.exception;

import com.example.j2n.impl.BaseMessage;

public class AccessDeniedException extends BaseServiceException {
    public AccessDeniedException(BaseMessage message) {
        super(message);
    }

    public AccessDeniedException(BaseMessage message, Throwable cause) {
        super(message, cause);
    }
}