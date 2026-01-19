package com.example.j2n.exception;

import com.example.j2n.impl.BaseMessage;

public class InvalidInputException extends BaseServiceException {
    public InvalidInputException(BaseMessage message) {
        super(message);
    }

    public InvalidInputException(BaseMessage message, Throwable cause) {
        super(message, cause);
    }
}
