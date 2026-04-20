package com.example.j2n.payment_srv.config;

import io.swagger.v3.oas.models.OpenAPI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.j2n.swagger.J2NOpenApiHelper;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return J2NOpenApiHelper.createDefaultOpenAPI("Payment Service API", "v1", "Payment APIs");
    }

}
