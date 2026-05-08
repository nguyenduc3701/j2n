package com.example.j2n.room_srv.constant;

import com.example.j2n.dto.SimpleBaseMessage;
import com.example.j2n.enums.HttpStatusCode;
import com.example.j2n.impl.BaseMessage;
import lombok.Getter;

@Getter
public enum MessageEnum implements BaseMessage {
    ROOM_NOT_FOUND("400901", HttpStatusCode.BAD_REQUEST, MessageConstants.ROOM_NOT_FOUND),
    BILL_NOT_FOUND("400902", HttpStatusCode.BAD_REQUEST, MessageConstants.BILL_NOT_FOUND),
    UTILITY_CONFIG_NOT_FOUND("400903", HttpStatusCode.BAD_REQUEST, MessageConstants.UTILITY_CONFIG_NOT_FOUND),
    RENTER_NOT_FOUND("400904", HttpStatusCode.BAD_REQUEST, MessageConstants.RENTER_NOT_FOUND),
    CALCULATE_BILL_ERROR("500901", HttpStatusCode.INTERNAL_SERVER_ERROR, MessageConstants.CALCULATE_BILL_ERROR);

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
        public static final String ROOM_NOT_FOUND = "Room not found with ID: %s";
        public static final String BILL_NOT_FOUND = "Bill not found with ID: %s";
        public static final String UTILITY_CONFIG_NOT_FOUND = "Utility configuration not found";
        public static final String RENTER_NOT_FOUND = "Renter information not found for ID: %s";
        public static final String CALCULATE_BILL_ERROR = "Error occurred while calculating room bill: %s";
    }
}
