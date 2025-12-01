package com.example.j2n.bff_srv.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.j2n.bff_srv.interceptor.JwtDecodeInterceptor;
import lombok.NonNull;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final JwtDecodeInterceptor jwtDecodeInterceptor;

    @Autowired
    public WebMvcConfig(JwtDecodeInterceptor jwtDecodeInterceptor) {
        this.jwtDecodeInterceptor = jwtDecodeInterceptor;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(jwtDecodeInterceptor)
                .excludePathPatterns("/api/bff/login", "/api/bff/register")
                .addPathPatterns("/**"); // áp dụng cho tất cả endpoint
    }
}
