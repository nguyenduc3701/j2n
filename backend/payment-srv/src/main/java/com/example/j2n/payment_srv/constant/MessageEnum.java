package com.example.j2n.payment_srv.constant;

import com.example.j2n.dto.SimpleBaseMessage;
import com.example.j2n.enums.HttpStatusCode;
import com.example.j2n.impl.BaseMessage;

import lombok.Getter;

@Getter
public enum MessageEnum implements BaseMessage {
    DELETE_CART_ITEMS_SHOULD_NOT_BE_EMPTY("400401", HttpStatusCode.BAD_REQUEST,
            MessageConstants.DELETE_CART_ITEMS_SHOULD_NOT_BE_EMPTY),
    CART_NOT_FOUND("400401", HttpStatusCode.BAD_REQUEST, MessageConstants.CART_NOT_FOUND);

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
        public static final String CART_NOT_FOUND = "No cart items found for user %s";
        public static final String DELETE_CART_ITEMS_SHOULD_NOT_BE_EMPTY = "Delete cart items should not be empty";
    }
}
