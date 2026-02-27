package com.example.j2n.api_gateway_srv.filter;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.lib.grpc.report.DashboardReportGrpcResponse;
import com.example.j2n.lib.grpc.report.ReportGrpcServiceGrpc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Empty;
import com.google.protobuf.util.JsonFormat;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class ReportGrpcFilterFactory extends AbstractGatewayFilterFactory<ReportGrpcFilterFactory.Config> {

    @GrpcClient("report-srv")
    private ReportGrpcServiceGrpc.ReportGrpcServiceBlockingStub reportServiceStub;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Bản đồ map từ Path sang Method xử lý gRPC
    private final Map<String, Function<org.springframework.web.server.ServerWebExchange, Mono<Object>>> methodMappings = new HashMap<>();

    public ReportGrpcFilterFactory() {
        super(Config.class);
        // Đăng ký các endpoint ở đây (Giống như Controller)
        methodMappings.put("/management/dashboard", this::callGetDashboardReport);
    }

    @Override
    public String name() {
        return "JsonToGRPC";
    }

    public static class Config {
        private String service;

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            // Xử lý logic gỡ bỏ prefix nếu cần (ví dụ StripPrefix=1 đã làm rồi thì path chỉ
            // còn /report/...)
            String subPath = path.substring(path.indexOf("/report") + 7);

            log.info("[API-GATEWAY] Grpc Routing subPath: {}", subPath);

            if (methodMappings.containsKey(subPath)) {
                return methodMappings.get(subPath).apply(exchange)
                        .flatMap(data -> writeJsonResponse(exchange, data));
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Object> callGetDashboardReport(org.springframework.web.server.ServerWebExchange exchange) {
        return Mono.fromCallable(() -> {
            try {
                DashboardReportGrpcResponse response = reportServiceStub.getDashboardReport(Empty.getDefaultInstance());
                String json = JsonFormat.printer().includingDefaultValueFields().print(response);
                return (Object) objectMapper.readValue(json, Map.class);
            } catch (StatusRuntimeException e) {
                log.error("[API-GATEWAY] gRPC Error calling report-srv: {}", e.getStatus());
                throw e;
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Void> writeJsonResponse(org.springframework.web.server.ServerWebExchange exchange, Object data) {
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        BaseResponse<Object> baseResponse = new BaseResponse<>();
        baseResponse.setCode("200");
        baseResponse.setMessage("Success");
        baseResponse.setData(data);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(baseResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
