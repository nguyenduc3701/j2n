package com.example.j2n.exception;

public class RetryableException extends BaseServiceException {
    public RetryableException(String message) {
        super(message);
    }

    public RetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}
