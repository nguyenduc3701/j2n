package com.example.j2n.image_srv.constant;

import com.example.j2n.dto.SimpleBaseMessage;
import com.example.j2n.enums.HttpStatusCode;
import com.example.j2n.impl.BaseMessage;

import lombok.Getter;

@Getter
public enum MessageEnum implements BaseMessage {
    IMAGE_NOT_FOUND("400401", HttpStatusCode.BAD_REQUEST, MessageConstants.IMAGE_NOT_FOUND),
    CAN_NOT_READ_STATIC_FILE("400402", HttpStatusCode.BAD_REQUEST, MessageConstants.CAN_NOT_READ_STATIC_FILE),
    FILE_SIZE_TOO_LARGE("400403", HttpStatusCode.BAD_REQUEST, MessageConstants.FILE_SIZE_TOO_LARGE),
    INVALID_FILE_TYPE("400404", HttpStatusCode.BAD_REQUEST, MessageConstants.INVALID_FILE_TYPE),
    INVALID_OWNER_TYPE("400405", HttpStatusCode.BAD_REQUEST, MessageConstants.INVALID_OWNER_TYPE);

    private final String code;
    private final HttpStatusCode httpStatus;
    private final String message;

    MessageEnum(String code, HttpStatusCode httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public BaseMessage withArgs(Object... args) {
        return new SimpleBaseMessage(this.code, this.httpStatus, String.format(this.message, args));
    }

    public static class MessageConstants {
        public static final String IMAGE_NOT_FOUND = "Image not found";
        public static final String CAN_NOT_READ_STATIC_FILE = "Can not read static file";
        public static final String FILE_SIZE_TOO_LARGE = "File size too large";
        public static final String INVALID_FILE_TYPE = "Invalid file type";
        public static final String INVALID_OWNER_TYPE = "Invalid owner type";
    }
}
