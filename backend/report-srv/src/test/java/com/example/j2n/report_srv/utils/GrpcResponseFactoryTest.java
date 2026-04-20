package com.example.j2n.report_srv.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.lib.proto.BaseProtoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.grpc.stub.StreamObserver;

public class GrpcResponseFactoryTest {

    private ObjectMapper objectMapper;
    private StreamObserver<BaseProtoResponse> streamObserver;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        objectMapper = new ObjectMapper();
        streamObserver = mock(StreamObserver.class);
    }

    @Test
    void testConstructor() {
        // Just to cover the default constructor for 100% coverage
        GrpcResponseFactory factory = new GrpcResponseFactory();
        assertNotNull(factory);
    }

    @Test
    void testMapToProtoResponse_ValidData() throws Exception {
        BaseResponse<TestData> baseResponse = new BaseResponse<>();
        baseResponse.setCode("200");
        baseResponse.setMessage("Success");
        baseResponse.setData(new TestData("test value"));

        BaseProtoResponse protoResponse = GrpcResponseFactory.mapToProtoResponse(baseResponse, objectMapper);

        assertEquals(200, protoResponse.getCode());
        assertEquals("Success", protoResponse.getMessage());
        assertTrue(protoResponse.hasData());
        assertEquals("test value", protoResponse.getData().getFieldsMap().get("field").getStringValue());
    }

    @Test
    void testMapToProtoResponse_NullCodeAndMessage() {
        // Arrange
        BaseResponse<Object> baseResponse = new BaseResponse<>();
        baseResponse.setCode(null);
        baseResponse.setMessage(null);

        // Act
        BaseProtoResponse protoResponse = GrpcResponseFactory.mapToProtoResponse(baseResponse, objectMapper);

        // Assert
        assertEquals(500, protoResponse.getCode()); // Default error
        assertEquals("", protoResponse.getMessage());
        assertFalse(protoResponse.hasData());
    }

    @Test
    void testMapToProtoResponse_InvalidNumericCode() {
        BaseResponse<Object> baseResponse = new BaseResponse<>();
        baseResponse.setCode("INVALID_CODE"); // not matching "\d+"

        BaseProtoResponse protoResponse = GrpcResponseFactory.mapToProtoResponse(baseResponse, objectMapper);

        assertEquals(500, protoResponse.getCode());
    }

    @Test
    void testMapToProtoResponse_ExceptionDuringMapping() throws Exception {
        BaseResponse<TestData> baseResponse = new BaseResponse<>();
        baseResponse.setCode("200");
        baseResponse.setData(new TestData("test value"));

        // Mock ObjectMapper to throw JsonProcessingException
        ObjectMapper mockMapper = mock(ObjectMapper.class);
        when(mockMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Test Exception") {
        });

        BaseProtoResponse protoResponse = GrpcResponseFactory.mapToProtoResponse(baseResponse, mockMapper);

        assertEquals(500, protoResponse.getCode());
        assertTrue(protoResponse.getMessage().contains("Internal Conversion Error: Test Exception"));
    }

    @Test
    void testOf_Success() {
        BaseResponse<TestData> baseResponse = new BaseResponse<>();
        baseResponse.setCode("200");

        GrpcResponseFactory.of(streamObserver, baseResponse, objectMapper);

        ArgumentCaptor<BaseProtoResponse> captor = ArgumentCaptor.forClass(BaseProtoResponse.class);
        verify(streamObserver).onNext(captor.capture());
        verify(streamObserver).onCompleted();

        assertEquals(200, captor.getValue().getCode());
    }

    @Test
    void testOf_ExceptionInObserver() {
        BaseResponse<TestData> baseResponse = new BaseResponse<>();

        // Cause exception when onNext is called
        doThrow(new RuntimeException("Observer exception")).when(streamObserver).onNext(any());

        GrpcResponseFactory.of(streamObserver, baseResponse, objectMapper);

        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        // Ensure onError is called and it must be RuntimeException holding
        // StatusRuntimeException
        verify(streamObserver).onError(throwableCaptor.capture());
        Throwable error = throwableCaptor.getValue();
        assertTrue(error instanceof io.grpc.StatusRuntimeException);
        io.grpc.StatusRuntimeException sre = (io.grpc.StatusRuntimeException) error;
        assertEquals(io.grpc.Status.Code.INTERNAL, sre.getStatus().getCode());
        assertTrue(sre.getStatus().getDescription().contains("Internal Error: Observer exception"));
    }

    private static class TestData {
        private String field;

        public TestData() {
        }

        public TestData(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}
