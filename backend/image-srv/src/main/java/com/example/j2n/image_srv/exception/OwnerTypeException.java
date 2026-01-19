package com.example.j2n.image_srv.exception;

import com.example.j2n.exception.BaseServiceException;
import com.example.j2n.image_srv.constant.MessageEnum;

public class OwnerTypeException extends BaseServiceException {
    public OwnerTypeException() {
        super(MessageEnum.INVALID_OWNER_TYPE);
    }
}
