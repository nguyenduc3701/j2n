package com.example.j2n.auth_srv.exception;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.BaseServiceException;
import com.example.j2n.impl.BaseMessage;
import com.example.j2n.utils.ResponseFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;

@RestControllerAdvice
@Slf4j
public class GlobalDefaultExceptionHandler {
        @ExceptionHandler(BaseServiceException.class)
        public ResponseEntity<BaseResponse<Object>> handleBaseServiceException(
                        BaseServiceException e) {
                log.error("[AUTH-SRV] {}", e.getMessage(), e);
                BaseMessage msg = e.getBaseMessage();
                return ResponseEntity
                                .status(msg.getHttpStatus().getCode())
                                .body(ResponseFactory.error(msg));
        }

        @ExceptionHandler(AuthorizationDeniedException.class)
        public ResponseEntity<BaseResponse<Object>> handleAuthorizationDeniedException(
                        AuthorizationDeniedException e) {
                log.error("[AUTH-SRV] Access Denied: {}", e.getMessage());
                return ResponseEntity
                                .status(com.example.j2n.enums.BaseMessageEnum.ACCESS_DENIED.getHttpStatus().getCode())
                                .body(ResponseFactory.error(com.example.j2n.enums.BaseMessageEnum.ACCESS_DENIED));
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<BaseResponse<Object>> handleAccessDeniedException(
                        AccessDeniedException e) {
                log.error("[AUTH-SRV] Access Denied: {}", e.getMessage());
                return ResponseEntity
                                .status(com.example.j2n.enums.BaseMessageEnum.ACCESS_DENIED.getHttpStatus().getCode())
                                .body(ResponseFactory.error(com.example.j2n.enums.BaseMessageEnum.ACCESS_DENIED));
        }
}
