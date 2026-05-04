package com.example.j2n.payment_srv.constant;

import com.example.j2n.dto.SimpleBaseMessage;
import com.example.j2n.enums.HttpStatusCode;
import com.example.j2n.impl.BaseMessage;

import lombok.Getter;

@Getter
public enum MessageEnum implements BaseMessage {
    DELETE_ORDER_ITEMS_SHOULD_NOT_BE_EMPTY("400401", HttpStatusCode.BAD_REQUEST,
            MessageConstants.DELETE_ORDER_ITEMS_SHOULD_NOT_BE_EMPTY),
    CART_NOT_FOUND("400401", HttpStatusCode.BAD_REQUEST, MessageConstants.CART_NOT_FOUND),
    ORDER_NOT_FOUND("400401", HttpStatusCode.BAD_REQUEST, MessageConstants.ORDER_NOT_FOUND),
    PRODUCT_NOT_FOUND("400402", HttpStatusCode.BAD_REQUEST, MessageConstants.PRODUCT_NOT_FOUND),
    INSUFFICIENT_STOCK("400403", HttpStatusCode.BAD_REQUEST, MessageConstants.INSUFFICIENT_STOCK),
    ORDER_AMOUNT_MISMATCH("400404", HttpStatusCode.BAD_REQUEST, MessageConstants.ORDER_AMOUNT_MISMATCH),
    PAYMENT_PROVIDER_ERROR("500401", HttpStatusCode.INTERNAL_SERVER_ERROR, MessageConstants.PAYMENT_PROVIDER_ERROR);

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
        public static final String DELETE_ORDER_ITEMS_SHOULD_NOT_BE_EMPTY = "Delete order items should not be empty";
        public static final String ORDER_NOT_FOUND = "Order not found";
        public static final String PRODUCT_NOT_FOUND = "Product with ID %s not found";
        public static final String INSUFFICIENT_STOCK = "Product %s is out of stock or insufficient quantity (Available: %s)";
        public static final String ORDER_AMOUNT_MISMATCH = "Total amount mismatch. Expected: %s, Actual: %s";
        public static final String PAYMENT_PROVIDER_ERROR = "Payment gateway error: %s";
    }
}
