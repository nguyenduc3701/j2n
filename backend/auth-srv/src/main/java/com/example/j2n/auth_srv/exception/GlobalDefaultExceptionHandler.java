package com.example.j2n.auth_srv.exception;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.auth_srv.constant.MessageEnum;
import com.example.j2n.utils.ResponseFactory;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
@Slf4j
public class GlobalDefaultExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleException(Exception e) {
        log.error("[AUTH-SRV] Exception: {}", e.getMessage(), e);
        return ResponseEntity.status(BaseMessageEnum.INTERNAL_ERROR.getHttpStatus().getCode())
                .body(ResponseFactory.error(BaseMessageEnum.INTERNAL_ERROR));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Object>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("[AUTH-SRV] AccessDeniedException: {}", e.getMessage(), e);
        return ResponseEntity.status(BaseMessageEnum.ACCESS_DENIED.getHttpStatus().getCode())
                .body(ResponseFactory.error(BaseMessageEnum.ACCESS_DENIED));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<BaseResponse<Object>> handleWebExchangeBindException(WebExchangeBindException e) {
        log.error("[AUTH-SRV] WebExchangeBindException: {}", e.getMessage(), e);
        return ResponseEntity.status(MessageEnum.FIELD_REQUIRED.getHttpStatus().getCode())
                .body(ResponseFactory.error(MessageEnum.FIELD_REQUIRED));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<BaseResponse<Object>> handleJwtException(JwtException e) {
        log.error("[AUTH-SRV] JwtException: {}", e.getMessage(), e);

        MessageEnum msg = e.getMessage() != null && e.getMessage().contains("expired")
                ? MessageEnum.TOKEN_EXPIRED
                : MessageEnum.TOKEN_INVALID;

        return ResponseEntity.status(msg.getHttpStatus().getCode())
                .body(ResponseFactory.error(msg));
    }
}
