package com.example.j2n.exception;

import com.example.j2n.enums.BaseMessageEnum;

public class UnknowFieldException extends BaseServiceException {
    public UnknowFieldException(BaseMessageEnum message) {
        super(message);
    }

    public UnknowFieldException(BaseMessageEnum message, Throwable cause) {
        super(message, cause);
    }
}
