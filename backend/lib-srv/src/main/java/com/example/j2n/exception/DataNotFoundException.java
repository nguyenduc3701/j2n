package com.example.j2n.exception;

import com.example.j2n.impl.BaseMessage;

public class DataNotFoundException extends BaseServiceException {
    public DataNotFoundException(BaseMessage message) {
        super(message);
    }

    public DataNotFoundException(BaseMessage message, Throwable cause) {
        super(message, cause);
    }
}
