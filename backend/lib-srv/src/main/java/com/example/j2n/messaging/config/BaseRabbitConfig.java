package com.example.j2n.messaging.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseRabbitConfig {
    /**
     * Dùng JSON cho TẤT CẢ message
     * Publisher & Consumer phải giống nhau
     */
    @Bean
    public Jackson2JsonMessageConverter jacksonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        // Priority: Use the inferred type from listener parameter instead of the __TypeId__ header
        // This solves the Package mismatch issue between services
        converter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        return converter;
    }
}
