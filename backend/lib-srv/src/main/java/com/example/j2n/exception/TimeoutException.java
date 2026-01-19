package com.example.j2n.exception;

import com.example.j2n.impl.BaseMessage;

public class TimeoutException extends BaseServiceException {
    public TimeoutException(BaseMessage message) {
        super(message);
    }

    public TimeoutException(BaseMessage message, Throwable cause) {
        super(message, cause);
    }
}
