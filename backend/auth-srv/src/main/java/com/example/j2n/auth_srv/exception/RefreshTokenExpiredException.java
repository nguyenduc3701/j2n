package com.example.j2n.auth_srv.exception;

import com.example.j2n.exception.BaseServiceException;
import com.example.j2n.auth_srv.constant.MessageEnum;
import com.example.j2n.impl.BaseMessage;

public class RefreshTokenExpiredException extends BaseServiceException {
    public RefreshTokenExpiredException() {
        super(MessageEnum.INVALID_REFRESH_TOKEN_EXPIRED);
    }

    public RefreshTokenExpiredException(BaseMessage message) {
        super(message);
    }
}
