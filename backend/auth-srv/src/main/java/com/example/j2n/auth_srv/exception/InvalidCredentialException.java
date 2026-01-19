package com.example.j2n.auth_srv.exception;

import com.example.j2n.exception.BaseServiceException;
import com.example.j2n.auth_srv.constant.MessageEnum;

public class InvalidCredentialException extends BaseServiceException {
    public InvalidCredentialException() {
        super(MessageEnum.INVALID_CREDENTIALS);
    }

    public InvalidCredentialException(com.example.j2n.impl.BaseMessage message) {
        super(message);
    }
}
