package com.example.j2n.report_srv.grpc;

import com.example.j2n.lib.grpc.report.*;
import com.example.j2n.report_srv.service.ManagementService;
import com.example.j2n.report_srv.service.response.DashboardReportResponse;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ManagementController extends ReportGrpcServiceGrpc.ReportGrpcServiceImplBase {

        private final ManagementService managementService;

        @Override
        public void getDashboardReport(Empty request, StreamObserver<DashboardReportGrpcResponse> responseObserver) {
                log.info("[REPORT-SRV] Handling gRPC getDashboardReport request");

                try {
                        DashboardReportGrpcResponse grpcResponse = managementService.getDashboardGrpcReport();
                        responseObserver.onNext(grpcResponse);
                        responseObserver.onCompleted();
                } catch (Exception e) {
                        log.error("[REPORT-SRV] Error handling gRPC request", e);
                        responseObserver.onError(io.grpc.Status.INTERNAL
                                        .withDescription(e.getMessage())
                                        .withCause(e)
                                        .asRuntimeException());
                }
        }
}
