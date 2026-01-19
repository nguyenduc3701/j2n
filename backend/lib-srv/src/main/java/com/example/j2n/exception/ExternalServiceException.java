package com.example.j2n.exception;

import com.example.j2n.impl.BaseMessage;

public class ExternalServiceException extends BaseServiceException {
    public ExternalServiceException(BaseMessage message) {
        super(message);
    }

    public ExternalServiceException(BaseMessage message, Throwable cause) {
        super(message, cause);
    }
}
