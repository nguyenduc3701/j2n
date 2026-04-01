package com.example.j2n.bff_srv.utils;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.utils.ResponseFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GraphQLFactoryTest {

    @Mock
    private HttpGraphQlClient client;

    @Mock
    private GraphQlClient.RequestSpec requestSpec;

    @Mock
    private GraphQlClient.RetrieveSpec retrieveSpec;

    @InjectMocks
    private GraphQLFactory graphQLFactory;

    @BeforeEach
    void setUp() {
        when(client.documentName(anyString())).thenReturn(requestSpec);
        when(requestSpec.operationName(anyString())).thenReturn(requestSpec);
        when(requestSpec.variables(anyMap())).thenReturn(requestSpec);
        when(requestSpec.retrieve(anyString())).thenReturn(retrieveSpec);
    }

    @Test
    void execute_ShouldReturnSuccess() {
        String docName = "doc";
        String path = "path";
        Map<String, Object> vars = Map.of("id", 1L);
        ParameterizedTypeReference<Object> type = new ParameterizedTypeReference<Object>() {};
        Object expectedResponse = new Object();

        when(retrieveSpec.toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        try (MockedStatic<RequestContextHolder> mockedContext = mockStatic(RequestContextHolder.class)) {
            mockedContext.when(RequestContextHolder::getRequestAttributes).thenReturn(mock(RequestAttributes.class));

            Mono<Object> result = graphQLFactory.execute(docName, path, vars, type);

            StepVerifier.create(result)
                    .expectNext(expectedResponse)
                    .verifyComplete();
        }

        verify(client).documentName(docName);
        verify(requestSpec).operationName("Op_" + path);
        verify(requestSpec).variables(vars);
        verify(requestSpec).retrieve(path);
        verify(retrieveSpec).toEntity(type);
    }

    @Test
    void execute_WithNullVars_ShouldUseEmptyMap() {
        String docName = "doc";
        String path = "path";
        ParameterizedTypeReference<Object> type = new ParameterizedTypeReference<Object>() {};
        Object expectedResponse = new Object();

        when(retrieveSpec.toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = graphQLFactory.execute(docName, path, null, type);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(requestSpec).variables(Map.of());
    }

    @Test
    @SuppressWarnings("unchecked")
    void execute_WithError_ShouldReturnBaseResponse() {
        String docName = "doc";
        String path = "path";
        ParameterizedTypeReference<Object> type = new ParameterizedTypeReference<Object>() {};
        RuntimeException exception = new RuntimeException("error");

        when(retrieveSpec.toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.error(exception));

        Mono<Object> result = graphQLFactory.execute(docName, path, null, type);

        StepVerifier.create(result)
                .expectNextMatches(resp -> resp instanceof BaseResponse && 
                                          ((BaseResponse<?>) resp).getCode().equals(String.valueOf(BaseMessageEnum.INTERNAL_ERROR.getHttpStatus().getCode())))
                .verifyComplete();
    }

    @Test
    void execute_WithNullAttributes_ShouldStillWork() {
        String docName = "doc";
        String path = "path";
        ParameterizedTypeReference<Object> type = new ParameterizedTypeReference<Object>() {};
        Object expectedResponse = new Object();

        when(retrieveSpec.toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        try (MockedStatic<RequestContextHolder> mockedContext = mockStatic(RequestContextHolder.class)) {
            mockedContext.when(RequestContextHolder::getRequestAttributes).thenReturn(null);

            Mono<Object> result = graphQLFactory.execute(docName, path, null, type);

            StepVerifier.create(result)
                    .expectNext(expectedResponse)
                    .verifyComplete();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void execute_WithErrorAndExceptionDuringErrorResponse_ShouldThrowOriginalError() {
        String docName = "doc";
        String path = "path";
        ParameterizedTypeReference<Object> type = new ParameterizedTypeReference<Object>() {};
        RuntimeException exception = new RuntimeException("original error");

        when(retrieveSpec.toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.error(exception));

        try (MockedStatic<ResponseFactory> mockedResponseFactory = mockStatic(ResponseFactory.class)) {
            mockedResponseFactory.when(() -> ResponseFactory.error(any())).thenThrow(new RuntimeException("factory error"));

            Mono<Object> result = graphQLFactory.execute(docName, path, null, type);

            StepVerifier.create(result)
                    .expectError(RuntimeException.class)
                    .verify();
        }
    }
}
