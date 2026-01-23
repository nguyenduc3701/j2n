package com.example.j2n.bff_srv.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.example.j2n.bff_srv.interceptor.RestTemplateRefreshInterceptor;

@Configuration
public class ApplicationConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateRefreshInterceptor refreshInterceptor) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(refreshInterceptor));
        return restTemplate;
    }

    @Bean(name = "authRestTemplate")
    public RestTemplate authRestTemplate() {
        return new RestTemplate();
    }
}
