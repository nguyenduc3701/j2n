package com.example.j2n.report_srv.exception;

import com.example.j2n.enums.HttpStatusCode;
import com.example.j2n.exception.BaseServiceException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@Slf4j
@GrpcAdvice
public class GrpcExceptionAdvice {

    @GrpcExceptionHandler(BaseServiceException.class)
    public StatusRuntimeException handleBaseServiceException(BaseServiceException e) {
        log.error("[GRPC-ERROR] BaseServiceException: {}", e.getMessage(), e);

        Status status = mapHttpStatusToGrpcStatus(e.getBaseMessage().getHttpStatus());

        return status
                .withDescription(e.getMessage())
                .asRuntimeException();
    }

    @GrpcExceptionHandler(Exception.class)
    public StatusRuntimeException handleException(Exception e) {
        log.error("[GRPC-ERROR] Unknown error: {}", e.getMessage(), e);
        return Status.INTERNAL
                .withDescription("Internal Server Error: " + e.getMessage())
                .asRuntimeException();
    }

    private Status mapHttpStatusToGrpcStatus(HttpStatusCode httpStatus) {
        if (httpStatus == null)
            return Status.INTERNAL;

        return switch (httpStatus) {
            case OK -> Status.OK;
            case BAD_REQUEST -> Status.INVALID_ARGUMENT;
            case UNAUTHORIZED -> Status.UNAUTHENTICATED;
            case FORBIDDEN -> Status.PERMISSION_DENIED;
            case NOT_FOUND -> Status.NOT_FOUND;
            default -> Status.INTERNAL;
        };
    }
}
