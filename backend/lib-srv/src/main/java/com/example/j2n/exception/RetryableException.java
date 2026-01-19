package com.example.j2n.exception;

import com.example.j2n.impl.BaseMessage;

public class RetryableException extends BaseServiceException {
    public RetryableException(BaseMessage message) {
        super(message);
    }

    public RetryableException(BaseMessage message, Throwable cause) {
        super(message, cause);
    }
}
