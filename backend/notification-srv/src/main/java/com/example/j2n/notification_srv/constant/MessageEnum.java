package com.example.j2n.notification_srv.constant;

import com.example.j2n.dto.SimpleBaseMessage;
import com.example.j2n.enums.HttpStatusCode;
import com.example.j2n.impl.BaseMessage;
import lombok.Getter;

@Getter
public enum MessageEnum implements BaseMessage {
    NOTIFICATION_NOT_FOUND("400101", HttpStatusCode.BAD_REQUEST, MessageConstants.NOTIFICATION_NOT_FOUND),
    SEND_EMAIL_SUCCESS("200101", HttpStatusCode.OK, MessageConstants.SEND_EMAIL_SUCCESS),
    SEND_EMAIL_FAILED("500101", HttpStatusCode.INTERNAL_SERVER_ERROR, MessageConstants.SEND_EMAIL_FAILED),
    MARK_AS_READ_SUCCESS("200102", HttpStatusCode.OK, MessageConstants.MARK_AS_READ_SUCCESS),
    MARK_ALL_AS_READ_SUCCESS("200103", HttpStatusCode.OK, MessageConstants.MARK_ALL_AS_READ_SUCCESS);

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
        public static final String NOTIFICATION_NOT_FOUND = "Notification not found with ID: %s";
        public static final String SEND_EMAIL_SUCCESS = "Email sent successfully";
        public static final String SEND_EMAIL_FAILED = "Failed to send email: %s";
        public static final String MARK_AS_READ_SUCCESS = "Notification marked as read";
        public static final String MARK_ALL_AS_READ_SUCCESS = "All notifications marked as read";
    }
}
