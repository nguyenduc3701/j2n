package com.example.j2n.image_srv.constant;

import com.example.j2n.enums.HttpStatusCode;
import com.example.j2n.impl.BaseMessage;

import lombok.Getter;

@Getter
public enum MessageEnum implements BaseMessage {
    IMAGE_NOT_FOUND("400401", HttpStatusCode.BAD_REQUEST, "Image not found"),
    CAN_NOT_READ_STATIC_FILE("400402", HttpStatusCode.BAD_REQUEST, "Can not read static file"),
    FILE_SIZE_TOO_LARGE("400403", HttpStatusCode.BAD_REQUEST, "File size too large"),
    INVVALID_FILE_TYPE("400404", HttpStatusCode.BAD_REQUEST, "Invalid file type");

    private final String code;
    private final HttpStatusCode httpStatus;
    private final String message;

    MessageEnum(String code, HttpStatusCode httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
