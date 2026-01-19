package com.example.j2n.exception;

import com.example.j2n.impl.BaseMessage;

public class SerializationException extends BaseServiceException {
    public SerializationException(BaseMessage message) {
        super(message);
    }

    public SerializationException(BaseMessage message, Throwable cause) {
        super(message, cause);
    }
}
