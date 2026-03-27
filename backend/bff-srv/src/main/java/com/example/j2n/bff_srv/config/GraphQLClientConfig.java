package com.example.j2n.bff_srv.config;

import com.example.j2n.bff_srv.interceptor.WebClientRefreshInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.RequiredArgsConstructor;

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
        return HttpGraphQlClient.builder(webClient).build();
    }
}
