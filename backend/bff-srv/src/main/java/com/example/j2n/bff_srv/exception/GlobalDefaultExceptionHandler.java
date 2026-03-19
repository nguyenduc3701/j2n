package com.example.j2n.bff_srv.exception;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.BaseServiceException;
import com.example.j2n.impl.BaseMessage;
import com.example.j2n.utils.ResponseFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalDefaultExceptionHandler {
    @ExceptionHandler(BaseServiceException.class)
    public ResponseEntity<BaseResponse<Object>> handleBaseServiceException(
            BaseServiceException e) {
        log.error("[BFF-SRV] {}", e.getMessage(), e);
        BaseMessage msg = e.getBaseMessage();
        return ResponseEntity
                .status(msg.getHttpStatus().getCode())
                .body(ResponseFactory.base(msg));
    }

    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    public ResponseEntity<BaseResponse<Object>> handleAuthorizationDeniedException(
            org.springframework.security.authorization.AuthorizationDeniedException e) {
        log.error("[BFF-SRV] Access Denied: {}", e.getMessage());
        return ResponseEntity
                .status(com.example.j2n.enums.BaseMessageEnum.ACCESS_DENIED.getHttpStatus().getCode())
                .body(ResponseFactory.error(com.example.j2n.enums.BaseMessageEnum.ACCESS_DENIED));
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Object>> handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException e) {
        log.error("[BFF-SRV] Access Denied: {}", e.getMessage());
        return ResponseEntity
                .status(com.example.j2n.enums.BaseMessageEnum.ACCESS_DENIED.getHttpStatus().getCode())
                .body(ResponseFactory.error(com.example.j2n.enums.BaseMessageEnum.ACCESS_DENIED));
    }
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidationExceptions(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(org.springframework.validation.FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Invalid input");
        log.error("[BFF-SRV] Validation failed: {}", errorMessage);
        return ResponseEntity
                .status(org.springframework.http.HttpStatus.BAD_REQUEST)
                .body(ResponseFactory.error(new com.example.j2n.dto.SimpleBaseMessage(
                        com.example.j2n.enums.BaseMessageEnum.BAD_REQUEST.getCode(),
                        com.example.j2n.enums.BaseMessageEnum.BAD_REQUEST.getHttpStatus(),
                        errorMessage)));
    }
}
