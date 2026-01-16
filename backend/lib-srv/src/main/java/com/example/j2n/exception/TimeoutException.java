package com.example.j2n.exception;

public class TimeoutException extends BaseServiceException {
    public TimeoutException(String message) {
        super(message);
    }

    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
