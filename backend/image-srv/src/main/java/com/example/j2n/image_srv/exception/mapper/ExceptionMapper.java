package com.example.j2n.image_srv.exception.mapper;

import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.exception.BaseServiceException;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.ExternalServiceException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.image_srv.constant.MessageEnum;
import com.example.j2n.image_srv.exception.StaticFileReadException;
import com.example.j2n.interfaces.BaseMessage;

public class ExceptionMapper {

    public static BaseMessage map(BaseServiceException e) {
        if (e instanceof StaticFileReadException) {
            return MessageEnum.CAN_NOT_READ_STATIC_FILE;
        }
        if (e instanceof InvalidInputException) {
            return BaseMessageEnum.INVALID_REQUEST;
        }
        if (e instanceof DataNotFoundException) {
            return BaseMessageEnum.NOT_FOUND;
        }
        if (e instanceof ExternalServiceException) {
            return BaseMessageEnum.EXTERNAL_SERVICE_ERROR;
        }

        return BaseMessageEnum.INTERNAL_ERROR;
    }

}
