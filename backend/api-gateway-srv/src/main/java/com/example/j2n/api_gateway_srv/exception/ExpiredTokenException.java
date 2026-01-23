package com.example.j2n.api_gateway_srv.exception;

import com.example.j2n.exception.BaseServiceException;
import com.example.j2n.api_gateway_srv.constant.MessageEnum;

public class ExpiredTokenException extends BaseServiceException {
    public ExpiredTokenException() {
        super(MessageEnum.TOKEN_EXPIRED);
    }
}
