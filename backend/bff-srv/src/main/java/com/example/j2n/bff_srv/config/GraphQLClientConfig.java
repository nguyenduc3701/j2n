package com.example.j2n.bff_srv.config;

import com.example.j2n.bff_srv.interceptor.WebClientRefreshInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.ClientGraphQlRequest;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.GraphQlClientInterceptor;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GraphQLClientConfig {

    @Value("${api-gateway.base-url}")
    private String gatewayBaseUrl;

    private final WebClientRefreshInterceptor webClientRefreshInterceptor;

    @Bean
    public HttpGraphQlClient travelGraphQlClient() {
        WebClient webClient = WebClient.builder()
                .baseUrl(gatewayBaseUrl + "/api/travel/graphql")
                .filter(webClientRefreshInterceptor)
                .build();

        return HttpGraphQlClient.builder(webClient)
                .interceptor(new GraphQlClientInterceptor() {
                    @Override
                    public Mono<ClientGraphQlResponse> intercept(ClientGraphQlRequest request, Chain chain) {
                        // 1. Log thông tin request trước khi gửi
                        log.info("🚀 [GraphQL Request] Operation: {}", request.getOperationName());
                        log.info("📦 [GraphQL Variables]: {}", request.getVariables());
                        log.debug("📜 [GraphQL Document Raw]: \n{}", request.getDocument());

                        // 2. Cho phép request đi tiếp và hứng kết quả để log error nếu có
                        return chain.next(request).doOnNext(response -> {
                            if (response.isValid()) {
                                log.info("✅ [GraphQL Response]: Success for operation '{}'",
                                        request.getOperationName());
                            } else {
                                log.error("❌ [GraphQL Response Errors]: {}", response.getErrors());
                            }
                        });
                    }
                })
                .build();
    }
}