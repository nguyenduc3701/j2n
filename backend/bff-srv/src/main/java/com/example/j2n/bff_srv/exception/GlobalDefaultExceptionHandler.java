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
}
