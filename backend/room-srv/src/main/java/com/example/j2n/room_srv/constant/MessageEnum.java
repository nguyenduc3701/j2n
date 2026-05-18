package com.example.j2n.room_srv.constant;

import com.example.j2n.dto.SimpleBaseMessage;
import com.example.j2n.enums.HttpStatusCode;
import com.example.j2n.impl.BaseMessage;
import lombok.Getter;

@Getter
public enum MessageEnum implements BaseMessage {
    ROOM_NOT_FOUND("400901", HttpStatusCode.BAD_REQUEST, MessageConstants.ROOM_NOT_FOUND),
    BILL_NOT_FOUND("400902", HttpStatusCode.BAD_REQUEST, MessageConstants.BILL_NOT_FOUND),
    FEE_NOT_FOUND("400903", HttpStatusCode.BAD_REQUEST, MessageConstants.FEE_NOT_FOUND),
    RENTER_NOT_FOUND("400904", HttpStatusCode.BAD_REQUEST, MessageConstants.RENTER_NOT_FOUND),
    ASSET_NOT_FOUND("400905", HttpStatusCode.BAD_REQUEST, MessageConstants.ASSET_NOT_FOUND),
    ASSET_ALREADY_EXISTS("400906", HttpStatusCode.BAD_REQUEST, MessageConstants.ASSET_ALREADY_EXISTS),
    MAP_ASSET_TO_ROOM_SUCCESS("200901", HttpStatusCode.OK, MessageConstants.MAP_ASSET_TO_ROOM_SUCCESS),
    INVALID_MONTH("400907", HttpStatusCode.BAD_REQUEST, MessageConstants.INVALID_MONTH),
    BILL_ALREADY_EXISTS("400908", HttpStatusCode.BAD_REQUEST, MessageConstants.BILL_ALREADY_EXISTS),
    INVALID_QUANTITY("400909", HttpStatusCode.BAD_REQUEST, MessageConstants.INVALID_QUANTITY),
    CALCULATE_BILL_ERROR("500901", HttpStatusCode.INTERNAL_SERVER_ERROR, MessageConstants.CALCULATE_BILL_ERROR),
    MEMBER_NOT_FOUND("400910", HttpStatusCode.BAD_REQUEST, MessageConstants.MEMBER_NOT_FOUND),
    MEMBER_ALREADY_MAPPED("400911", HttpStatusCode.BAD_REQUEST, MessageConstants.MEMBER_ALREADY_MAPPED),
    MEMBER_ALREADY_MAPPED_TO_ANOTHER_ROOM("400912", HttpStatusCode.BAD_REQUEST, MessageConstants.MEMBER_ALREADY_MAPPED_TO_ANOTHER_ROOM),
    ROOM_MAX_PEOPLE_EXCEEDED("400913", HttpStatusCode.BAD_REQUEST, MessageConstants.ROOM_MAX_PEOPLE_EXCEEDED),
    MAP_MEMBER_TO_ROOM_SUCCESS("200902", HttpStatusCode.OK, MessageConstants.MAP_MEMBER_TO_ROOM_SUCCESS),
    DELETE_MEMBER_SUCCESS("200903", HttpStatusCode.OK, MessageConstants.DELETE_MEMBER_SUCCESS),
    UPDATE_MEMBER_SUCCESS("200904", HttpStatusCode.OK, MessageConstants.UPDATE_MEMBER_SUCCESS);

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
        public static final String FEE_NOT_FOUND = "Fee configuration not found";
        public static final String RENTER_NOT_FOUND = "Renter information not found for ID: %s";
        public static final String ASSET_NOT_FOUND = "Asset not found with ID: %s";
        public static final String ASSET_ALREADY_EXISTS = "Asset already exists with name: %s";
        public static final String MAP_ASSET_TO_ROOM_SUCCESS = "Mapped asset to room successfully";
        public static final String INVALID_MONTH = "Invalid month specified: %s";
        public static final String BILL_ALREADY_EXISTS = "Bill already exists for month: %s";
        public static final String INVALID_QUANTITY = "Quantity must be at least 1";
        public static final String CALCULATE_BILL_ERROR = "Error occurred while calculating room bill: %s";
        public static final String MEMBER_NOT_FOUND = "Room member not found with ID: %s";
        public static final String MEMBER_ALREADY_MAPPED = "User with ID %s is already a member of Room with ID %s";
        public static final String MEMBER_ALREADY_MAPPED_TO_ANOTHER_ROOM = "User with ID %s is already a member of another Room with ID %s";
        public static final String ROOM_MAX_PEOPLE_EXCEEDED = "Room maximum capacity exceeded. Max people allowed: %s";
        public static final String MAP_MEMBER_TO_ROOM_SUCCESS = "Mapped member to room successfully";
        public static final String DELETE_MEMBER_SUCCESS = "Deleted room member successfully";
        public static final String UPDATE_MEMBER_SUCCESS = "Updated room member successfully";
    }
}
