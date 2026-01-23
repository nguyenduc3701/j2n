package com.example.j2n.api_gateway_srv.exception;

import com.example.j2n.api_gateway_srv.constant.MessageEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ResponseStatusException;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalErrorHandlerTest {

    private GlobalErrorHandler globalErrorHandler;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        globalErrorHandler = new GlobalErrorHandler(objectMapper);
    }

    @Test
    void handle_ShouldReturnInternalServerError_WhenGenericException() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        RuntimeException ex = new RuntimeException("Generic error");

        StepVerifier.create(globalErrorHandler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getResponse().getStatusCode());
        assertEquals("application/json", exchange.getResponse().getHeaders().getContentType().toString());
    }

    @Test
    void handle_ShouldReturnTooManyRequests_WhenResponseStatusException() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");

        StepVerifier.create(globalErrorHandler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, exchange.getResponse().getStatusCode());
    }

    @Test
    void handle_ShouldReturnOtherStatus_WhenResponseStatusException() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");

        StepVerifier.create(globalErrorHandler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.NOT_FOUND, exchange.getResponse().getStatusCode());
    }
}
