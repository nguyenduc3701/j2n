package com.example.j2n.report_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.lib.proto.GetDashboardRequest;
import com.example.j2n.lib.proto.ReportResponse;
import com.example.j2n.lib.proto.ReportServiceGrpc;
import com.example.j2n.report_srv.service.response.DashboardReportResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ManagementGrpcService extends ReportServiceGrpc.ReportServiceImplBase {

    private final ManagementService managementService;
    private final ObjectMapper objectMapper;

    @Override
    public void getDashboardReport(GetDashboardRequest request, StreamObserver<ReportResponse> responseObserver) {
        log.info("[GRPC] Requesting dashboard report");

        BaseResponse<DashboardReportResponse> baseResponse = managementService.getDashboardReport();

        try {
            String jsonData = objectMapper.writeValueAsString(baseResponse.getData());
            int statusValue = 200;
            if (baseResponse.getCode() != null && baseResponse.getCode().matches("\\d+")) {
                statusValue = Integer.parseInt(baseResponse.getCode());
            }

            ReportResponse response = ReportResponse.newBuilder()
                    .setStatus(Int32Value.of(statusValue))
                    .setMessage(StringValue.of(baseResponse.getMessage()))
                    .setData(StringValue.of(jsonData))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (JsonProcessingException e) {
            log.error("[GRPC] Error serializing dashboard report", e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Error serializing response")
                    .asRuntimeException());
        }
    }
}
