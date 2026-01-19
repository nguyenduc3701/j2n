package com.example.j2n.image_srv.exception;

import com.example.j2n.exception.BaseServiceException;
import com.example.j2n.image_srv.constant.MessageEnum;

public class FileSizeException extends BaseServiceException {
    public FileSizeException() {
        super(MessageEnum.FILE_SIZE_TOO_LARGE);
    }
}
