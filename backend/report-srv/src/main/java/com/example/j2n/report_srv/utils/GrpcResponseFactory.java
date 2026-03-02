package com.example.j2n.report_srv.utils;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.lib.proto.BaseProtoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GrpcResponseFactory {

    /**
     * Map a standard BaseResponse to a gRPC BaseProtoResponse.
     * 
     * @param baseResponse The source BaseResponse
     * @param objectMapper ObjectMapper to serialize the data object
     * @return A built BaseProtoResponse
     */
    public static BaseProtoResponse mapToProtoResponse(BaseResponse<?> baseResponse, ObjectMapper objectMapper) {
        try {
            BaseProtoResponse.Builder builder = BaseProtoResponse.newBuilder();

            // Set code
            if (baseResponse.getCode() != null && baseResponse.getCode().matches("\\d+")) {
                builder.setCode(Integer.parseInt(baseResponse.getCode()));
            } else {
                builder.setCode(500); // Default error if no code
            }

            // Set message
            builder.setMessage(baseResponse.getMessage() != null ? baseResponse.getMessage() : "");

            // Set data (convert to Struct)
            if (baseResponse.getData() != null) {
                String jsonData = objectMapper.writeValueAsString(baseResponse.getData());
                Struct.Builder structBuilder = Struct.newBuilder();
                JsonFormat.parser().merge(jsonData, structBuilder);
                builder.setData(structBuilder.build());
            }

            return builder.build();
        } catch (Exception e) {
            log.error("[GRPC-RESPONSE-FACTORY] Error mapping response: ", e);
            return BaseProtoResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Internal Conversion Error: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Send a BaseResponse directly to a gRPC StreamObserver.
     * 
     * @param observer     gRPC StreamObserver
     * @param baseResponse Source BaseResponse
     * @param objectMapper ObjectMapper
     */
    public static void of(StreamObserver<BaseProtoResponse> observer, BaseResponse<?> baseResponse,
            ObjectMapper objectMapper) {
        try {
            observer.onNext(mapToProtoResponse(baseResponse, objectMapper));
            observer.onCompleted();
        } catch (Exception e) {
            log.error("[GRPC-RESPONSE-FACTORY] Fatal error in gRPC observer: ", e);
            observer.onError(io.grpc.Status.INTERNAL
                    .withCause(e)
                    .withDescription("Internal Error: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}
