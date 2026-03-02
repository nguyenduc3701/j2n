package com.example.j2n.report_srv.config;

import com.example.j2n.report_srv.interceptor.GrpcServerAuthInterceptor;
import net.devh.boot.grpc.server.interceptor.GlobalServerInterceptorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcInterceptorConfig {

    @Bean
    public GlobalServerInterceptorConfigurer authInterceptorRegistry(GrpcServerAuthInterceptor interceptor) {
        return registry -> registry.add(interceptor);
    }
}
