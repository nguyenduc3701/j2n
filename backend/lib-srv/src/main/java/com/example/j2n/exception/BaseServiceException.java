package com.example.j2n.exception;

public abstract class BaseServiceException extends RuntimeException {
    protected BaseServiceException(String message) {
        super(message);
    }

    protected BaseServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}