package com.example.j2n.image_srv.exception;

import com.example.j2n.exception.BaseServiceException;
import com.example.j2n.image_srv.constant.MessageEnum;

public class FileTypeException extends BaseServiceException {
    public FileTypeException() {
        super(MessageEnum.INVALID_FILE_TYPE);
    }
}
