package com.example.j2n.travel_srv.constant;

import com.example.j2n.dto.SimpleBaseMessage;
import com.example.j2n.enums.HttpStatusCode;
import com.example.j2n.impl.BaseMessage;

import lombok.Getter;

@Getter
public enum MessageEnum implements BaseMessage {
    CATEGORY_NOT_FOUND("400401", HttpStatusCode.BAD_REQUEST, MessageConstants.CATEGORY_NOT_FOUND),
    TOUR_NOT_FOUND("400402", HttpStatusCode.BAD_REQUEST, MessageConstants.TOUR_NOT_FOUND),
    TOUR_IMAGE_NOT_FOUND("400403", HttpStatusCode.BAD_REQUEST, MessageConstants.TOUR_IMAGE_NOT_FOUND);

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
        public static final String CATEGORY_NOT_FOUND = "Category not found";
        public static final String TOUR_NOT_FOUND = "Tour not found";
        public static final String TOUR_IMAGE_NOT_FOUND = "Tour image not found";
    }
}
