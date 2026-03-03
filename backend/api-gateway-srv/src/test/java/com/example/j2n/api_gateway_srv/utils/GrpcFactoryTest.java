package com.example.j2n.api_gateway_srv.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Empty;
import com.google.protobuf.Message;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrpcFactoryTest {

    private GrpcFactory grpcFactory;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        grpcFactory = new GrpcFactory(objectMapper);
    }

    @Test
    void invokeAndWrap_Success() {
        StepVerifier.create(grpcFactory.invokeAndWrap(() -> "{\"status\":\"ok\"}", "TestService"))
                .expectNextMatches(response -> response.getStatusCode().is2xxSuccessful()
                        && "{\"status\":\"ok\"}".equals(response.getBody()))
                .verifyComplete();
    }

    @Test
    void invokeAndWrap_Error() {
        StepVerifier.create(grpcFactory.invokeAndWrap(() -> {
            throw new RuntimeException("error");
        }, "TestService"))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void createManagedChannel_Success() throws Exception {
        ManagedChannel channel = grpcFactory.createManagedChannel("http://localhost:18184");
        assertNotNull(channel);
        channel.shutdown();
    }

    @Test
    void convertMultipartToJson_Success() {
        MultiValueMap<String, Part> parts = new LinkedMultiValueMap<>();
        FormFieldPart field1 = mock(FormFieldPart.class);
        when(field1.value()).thenReturn("value1");
        parts.add("key1", field1);

        FilePart filePart = mock(FilePart.class);
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        var buffer = factory.wrap("test".getBytes());

        when(filePart.content()).thenReturn(reactor.core.publisher.Flux.just(buffer));
        parts.add("file1", filePart);

        StepVerifier.create(grpcFactory.convertMultipartToJson(parts))
                .expectNextMatches(json -> json.contains("\"key1\":\"value1\"") && json.contains("\"file1\":\""))
                .verifyComplete();
    }

    @Test
    void convertMultipartToJson_UnknownPart() {
        MultiValueMap<String, Part> parts = new LinkedMultiValueMap<>();
        Part unknownPart = mock(Part.class);
        parts.add("unknown", unknownPart);

        StepVerifier.create(grpcFactory.convertMultipartToJson(parts))
                .expectNext("{}")
                .verifyComplete();
    }

    public static class MockGrpcStub {
        public Message testMethod(Empty request) {
            return Empty.getDefaultInstance();
        }

        public Message errorMethod(Empty request) {
            throw new RuntimeException("RPC error");
        }
    }

    @Test
    void dynamicInvoke_Success() throws Exception {
        MockGrpcStub stub = new MockGrpcStub();

        // 1. Test case: cache miss
        String result1 = grpcFactory.dynamicInvoke(stub, "testMethod", "{}");
        assertTrue(result1.replaceAll("\\s", "").contains("{}"));

        // 2. Test case: cache hit
        String result2 = grpcFactory.dynamicInvoke(stub, "testMethod", null);
        assertTrue(result2.replaceAll("\\s", "").contains("{}"));
    }

    @Test
    void dynamicInvoke_EmptyJsonBody() throws Exception {
        MockGrpcStub stub = new MockGrpcStub();
        String result = grpcFactory.dynamicInvoke(stub, "testMethod", "");
        assertTrue(result.replaceAll("\\s", "").contains("{}"));
    }

    @Test
    void dynamicInvoke_ValidJsonBody() throws Exception {
        MockGrpcStub stub = new MockGrpcStub();
        String result = grpcFactory.dynamicInvoke(stub, "testMethod", "{\"someField\":\"someValue\"}");
        assertTrue(result.replaceAll("\\s", "").contains("{}"));
    }

    @Test
    void dynamicInvoke_ReflectionError() {
        MockGrpcStub stub = new MockGrpcStub();
        assertThrows(Exception.class, () -> grpcFactory.dynamicInvoke(stub, "errorMethod", "{}"));
    }

    @Test
    void dynamicInvoke_MethodNotFound() {
        Object stub = new Object();
        assertThrows(NoSuchMethodException.class, () -> grpcFactory.dynamicInvoke(stub, "unknownMethod", "{}"));
    }
}
