package com.example.j2n.image_srv.exception;

import com.example.j2n.exception.BaseServiceException;
import com.example.j2n.image_srv.constant.MessageEnum;

public class StaticFileReadException extends BaseServiceException {

    public StaticFileReadException(Throwable cause) {
        super(MessageEnum.CAN_NOT_READ_STATIC_FILE, cause);
    }
}
