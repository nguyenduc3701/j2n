package com.example.j2n.bff_srv.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@Getter
public class GatewayConfig {
    @Value("${api-gateway.base-url}")
    private String baseUrl;
}
