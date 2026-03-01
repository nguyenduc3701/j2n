package com.example.j2n.api_gateway_srv.controller;

import com.example.j2n.api_gateway_srv.constant.MessageEnum;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.utils.ResponseFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/report")
    public Mono<ResponseEntity<BaseResponse<Object>>> reportFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ResponseFactory.error(MessageEnum.INTERNAL_SERVER_ERROR)));
    }
}
