package com.example.j2n.auth_srv.exception;

import com.example.j2n.exception.BaseServiceException;
import com.example.j2n.auth_srv.constant.MessageEnum;
import com.example.j2n.impl.BaseMessage;

public class InvalidRefreshTokenException extends BaseServiceException {
    public InvalidRefreshTokenException() {
        super(MessageEnum.INVALID_REFRESH_TOKEN);
    }

    public InvalidRefreshTokenException(BaseMessage message) {
        super(message);
    }
}
