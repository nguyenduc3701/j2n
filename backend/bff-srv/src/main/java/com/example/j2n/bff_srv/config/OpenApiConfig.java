package com.example.j2n.bff_srv.config;

import com.example.j2n.swagger.J2NOpenApiHelper;
import io.swagger.v3.oas.models.OpenAPI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return J2NOpenApiHelper.createDefaultOpenAPI("BFF Service API", "v1", "BFF Service APIs");
    }
}
