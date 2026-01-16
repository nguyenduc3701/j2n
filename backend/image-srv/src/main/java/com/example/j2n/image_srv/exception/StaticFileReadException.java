package com.example.j2n.image_srv.exception;

import com.example.j2n.exception.BaseServiceException;

public class StaticFileReadException extends BaseServiceException {

    public StaticFileReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
