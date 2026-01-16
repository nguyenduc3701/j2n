package com.example.j2n.image_srv.exception;

import com.example.j2n.image_srv.exception.mapper.ExceptionMapper;
import com.example.j2n.interfaces.BaseMessage;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
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
        BaseMessage msg = ExceptionMapper.map(e);
        return ResponseEntity
                .status(msg.getHttpStatus().getCode())
                .body(ResponseFactory.error(msg));
    }
}
