package com.example.j2n.api_gateway_srv.exception;

import com.example.j2n.api_gateway_srv.constant.MessageEnum;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.BaseServiceException;
import com.example.j2n.impl.BaseMessage;
import com.example.j2n.utils.ResponseFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.StatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
@RequiredArgsConstructor
@Slf4j
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        if (ex instanceof BaseServiceException bse) {
            BaseMessage msg = bse.getBaseMessage();
            exchange.getResponse().setStatusCode(HttpStatus.valueOf(msg.getHttpStatus().getCode()));
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            BaseResponse<Object> response = ResponseFactory.error(msg);
            return write(exchange, response);
        }

        if (ex instanceof ResponseStatusException rse && rse.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            BaseResponse<Object> response = ResponseFactory.error(MessageEnum.TOO_MANY_REQUEST);
            return write(exchange, response);
        }

        // Handle gRPC exception
        Throwable cause = ex;
        if (ex.getCause() != null
                && (ex.getCause() instanceof StatusRuntimeException || ex.getCause() instanceof StatusException)) {
            cause = ex.getCause();
        }

        if (cause instanceof StatusRuntimeException || cause instanceof StatusException) {
            Status status = (cause instanceof StatusRuntimeException) ? ((StatusRuntimeException) cause).getStatus()
                    : ((StatusException) cause).getStatus();

            HttpStatus mappedStatus = mapGrpcStatusToHttp(status);
            log.error("[GATEWAY-ERROR] gRPC error: {} -> {}", status.getCode(), mappedStatus);

            exchange.getResponse().setStatusCode(mappedStatus);
            return write(exchange, ResponseFactory.error(MessageEnum.INTERNAL_SERVER_ERROR));
        }

        // fallback
        log.error("[GATEWAY-ERROR] Unhandled exception: ", ex);
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return write(exchange, ResponseFactory.error(MessageEnum.INTERNAL_SERVER_ERROR));
    }

    private HttpStatus mapGrpcStatusToHttp(Status status) {
        return switch (status.getCode()) {
            case OK -> HttpStatus.OK;
            case INVALID_ARGUMENT, FAILED_PRECONDITION, OUT_OF_RANGE -> HttpStatus.BAD_REQUEST;
            case UNAUTHENTICATED -> HttpStatus.UNAUTHORIZED;
            case PERMISSION_DENIED -> HttpStatus.FORBIDDEN;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case ALREADY_EXISTS, ABORTED -> HttpStatus.CONFLICT;
            case RESOURCE_EXHAUSTED -> HttpStatus.TOO_MANY_REQUESTS;
            case UNIMPLEMENTED -> HttpStatus.NOT_IMPLEMENTED;
            case UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            case DEADLINE_EXCEEDED -> HttpStatus.GATEWAY_TIMEOUT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private Mono<Void> write(ServerWebExchange exchange, BaseResponse<?> body) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
