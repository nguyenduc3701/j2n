package com.example.j2n.order_srv.constant;

import com.example.j2n.dto.SimpleBaseMessage;
import com.example.j2n.enums.HttpStatusCode;
import com.example.j2n.impl.BaseMessage;

import lombok.Getter;

@Getter
public enum MessageEnum implements BaseMessage {
    DELETE_ORDER_ITEMS_SHOULD_NOT_BE_EMPTY("400801", HttpStatusCode.BAD_REQUEST,
            MessageConstants.DELETE_ORDER_ITEMS_SHOULD_NOT_BE_EMPTY),
    ORDER_NOT_FOUND("400801", HttpStatusCode.BAD_REQUEST, MessageConstants.ORDER_NOT_FOUND),
    ADD_TO_ORDER_SUCCESS("200801", HttpStatusCode.OK, MessageConstants.ADD_TO_ORDER_SUCCESS),
    UPDATE_ORDER_SUCCESS("200801", HttpStatusCode.OK, MessageConstants.UPDATE_ORDER_SUCCESS);

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
        public static final String ADD_TO_ORDER_SUCCESS = "Item added to order successfully";
        public static final String UPDATE_ORDER_SUCCESS = "Order updated successfully";
        public static final String ORDER_NOT_FOUND = "No order items found for user %s";
        public static final String DELETE_ORDER_ITEMS_SHOULD_NOT_BE_EMPTY = "Delete order items should not be empty";
    }
}
