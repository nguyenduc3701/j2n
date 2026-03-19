package com.example.j2n.image_srv.exception;

import com.example.j2n.impl.BaseMessage;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.BaseServiceException;
import com.example.j2n.utils.ResponseFactory;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseServiceException.class)
    public ResponseEntity<BaseResponse<Object>> handleBaseServiceException(
            BaseServiceException e) {
        log.error("[IMAGE-SRV] {}", e.getMessage(), e);
        BaseMessage msg = e.getBaseMessage();
        return ResponseEntity
                .status(msg.getHttpStatus().getCode())
                .body(ResponseFactory.error(msg));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        log.error("[IMAGE-SRV] Validation error: {}", errorMessage);
        return ResponseEntity
                .status(com.example.j2n.enums.BaseMessageEnum.BAD_REQUEST.getHttpStatus().getCode())
                .body(ResponseFactory.error(com.example.j2n.enums.BaseMessageEnum.BAD_REQUEST.withArgs(errorMessage)));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<BaseResponse<Object>> handleBindException(BindException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        log.error("[IMAGE-SRV] Bind error: {}", errorMessage);
        return ResponseEntity
                .status(com.example.j2n.enums.BaseMessageEnum.BAD_REQUEST.getHttpStatus().getCode())
                .body(ResponseFactory.error(com.example.j2n.enums.BaseMessageEnum.BAD_REQUEST.withArgs(errorMessage)));
    }

    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    public ResponseEntity<BaseResponse<Object>> handleAuthorizationDeniedException(
            org.springframework.security.authorization.AuthorizationDeniedException e) {
        log.error("[IMAGE-SRV] Access Denied: {}", e.getMessage());
        return ResponseEntity
                .status(com.example.j2n.enums.BaseMessageEnum.ACCESS_DENIED.getHttpStatus().getCode())
                .body(ResponseFactory.error(com.example.j2n.enums.BaseMessageEnum.ACCESS_DENIED));
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Object>> handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException e) {
        log.error("[IMAGE-SRV] Access Denied: {}", e.getMessage());
        return ResponseEntity
                .status(com.example.j2n.enums.BaseMessageEnum.ACCESS_DENIED.getHttpStatus().getCode())
                .body(ResponseFactory.error(com.example.j2n.enums.BaseMessageEnum.ACCESS_DENIED));
    }
}
