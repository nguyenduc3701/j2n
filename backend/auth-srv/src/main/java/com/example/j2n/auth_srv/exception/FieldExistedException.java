package com.example.j2n.auth_srv.exception;

import com.example.j2n.exception.BaseServiceException;
import com.example.j2n.auth_srv.constant.MessageEnum;

public class FieldExistedException extends BaseServiceException {
    public FieldExistedException(String fieldName) {
        super(MessageEnum.FIELD_EXISTED.withArgs(fieldName));
    }

    public FieldExistedException(com.example.j2n.impl.BaseMessage message) {
        super(message);
    }
}
