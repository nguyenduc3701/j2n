package com.example.j2n.auth_srv.exception;

import java.nio.file.AccessDeniedException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import lombok.extern.slf4j.Slf4j;
import com.example.j2n.auth_srv.service.response.BaseResponse;
import com.example.j2n.auth_srv.utils.ResponseFactory;
import com.example.j2n.auth_srv.constant.MessageEnum;
import io.jsonwebtoken.JwtException;

@RestControllerAdvice
@Slf4j
public class GlobalDefaultExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleException(Exception e) {
        log.error("[AUTH-SRV] Exception: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseFactory.error(MessageEnum.INTERNAL_ERROR));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Object>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("[AUTH-SRV] AccessDeniedException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseFactory.error(MessageEnum.ACCESS_DENIED));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<BaseResponse<Object>> handleMethodArgumentNotValidException(WebExchangeBindException e) {
        log.error("[AUTH-SRV] WebExchangeBindException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseFactory.error(MessageEnum.FIELD_REQUIRED));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<BaseResponse<Object>> handleAuthenticationException(JwtException e) {
        log.error("[AUTH-SRV] JwtException: {}", e.getMessage());
        MessageEnum msg;
        if (e.getMessage().contains("expired")) {
            msg = MessageEnum.TOKEN_EXPIRED;
        } else {
            msg = MessageEnum.TOKEN_INVALID;
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseFactory.error(msg));
    }
}
