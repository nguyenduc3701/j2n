package com.example.j2n.product_srv.constant;

import com.example.j2n.dto.SimpleBaseMessage;
import com.example.j2n.enums.HttpStatusCode;
import com.example.j2n.impl.BaseMessage;

import lombok.Getter;

@Getter
public enum MessageEnum implements BaseMessage {
    CATEGORY_NOT_FOUND("400401", HttpStatusCode.BAD_REQUEST, MessageConstants.CATEGORY_NOT_FOUND),
    PRODUCT_NOT_FOUND("400402", HttpStatusCode.BAD_REQUEST, MessageConstants.PRODUCT_NOT_FOUND),
    PRODUCT_IMAGE_NOT_FOUND("400403", HttpStatusCode.BAD_REQUEST, MessageConstants.PRODUCT_IMAGE_NOT_FOUND),
    PRODUCT_SCHEDULE_NOT_FOUND("400404", HttpStatusCode.BAD_REQUEST, MessageConstants.PRODUCT_SCHEDULE_NOT_FOUND),
    
    CREATE_CATEGORY_SUCCESS("200401", HttpStatusCode.OK, MessageConstants.CREATE_CATEGORY_SUCCESS),
    UPDATE_CATEGORY_SUCCESS("200402", HttpStatusCode.OK, MessageConstants.UPDATE_CATEGORY_SUCCESS),
    DELETE_CATEGORY_SUCCESS("200403", HttpStatusCode.OK, MessageConstants.DELETE_CATEGORY_SUCCESS),
    
    CREATE_PRODUCT_SUCCESS("200411", HttpStatusCode.OK, MessageConstants.CREATE_PRODUCT_SUCCESS),
    UPDATE_PRODUCT_SUCCESS("200412", HttpStatusCode.OK, MessageConstants.UPDATE_PRODUCT_SUCCESS),
    DELETE_PRODUCT_SUCCESS("200413", HttpStatusCode.OK, MessageConstants.DELETE_PRODUCT_SUCCESS),
    
    CREATE_PRODUCT_IMAGE_SUCCESS("200421", HttpStatusCode.OK, MessageConstants.CREATE_PRODUCT_IMAGE_SUCCESS),
    UPDATE_PRODUCT_IMAGE_SUCCESS("200422", HttpStatusCode.OK, MessageConstants.UPDATE_PRODUCT_IMAGE_SUCCESS),
    DELETE_PRODUCT_IMAGE_SUCCESS("200423", HttpStatusCode.OK, MessageConstants.DELETE_PRODUCT_IMAGE_SUCCESS),
    
    CREATE_PRODUCT_SCHEDULE_SUCCESS("200431", HttpStatusCode.OK, MessageConstants.CREATE_PRODUCT_SCHEDULE_SUCCESS),
    UPDATE_PRODUCT_SCHEDULE_SUCCESS("200432", HttpStatusCode.OK, MessageConstants.UPDATE_PRODUCT_SCHEDULE_SUCCESS),
    DELETE_PRODUCT_SCHEDULE_SUCCESS("200433", HttpStatusCode.OK, MessageConstants.DELETE_PRODUCT_SCHEDULE_SUCCESS);

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
        public static final String PRODUCT_NOT_FOUND = "Product not found";
        public static final String PRODUCT_IMAGE_NOT_FOUND = "Product image not found";
        public static final String PRODUCT_SCHEDULE_NOT_FOUND = "Product schedule not found";
        
        public static final String CREATE_CATEGORY_SUCCESS = "Created category successfully";
        public static final String UPDATE_CATEGORY_SUCCESS = "Updated category successfully";
        public static final String DELETE_CATEGORY_SUCCESS = "Deleted category successfully";
        
        public static final String CREATE_PRODUCT_SUCCESS = "Created product successfully";
        public static final String UPDATE_PRODUCT_SUCCESS = "Updated product successfully";
        public static final String DELETE_PRODUCT_SUCCESS = "Deleted product successfully";
        
        public static final String CREATE_PRODUCT_IMAGE_SUCCESS = "Added product image successfully";
        public static final String UPDATE_PRODUCT_IMAGE_SUCCESS = "Updated product image successfully";
        public static final String DELETE_PRODUCT_IMAGE_SUCCESS = "Deleted product image successfully";
        
        public static final String CREATE_PRODUCT_SCHEDULE_SUCCESS = "Added product schedule successfully";
        public static final String UPDATE_PRODUCT_SCHEDULE_SUCCESS = "Updated product schedule successfully";
        public static final String DELETE_PRODUCT_SCHEDULE_SUCCESS = "Deleted product schedule successfully";
    }
}
