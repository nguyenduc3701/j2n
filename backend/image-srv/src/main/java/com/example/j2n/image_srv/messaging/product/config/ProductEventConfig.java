package com.example.j2n.image_srv.messaging.product.config;

import com.example.j2n.image_srv.messaging.product.constant.ProductEventConstants;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductEventConfig {
    @Bean
    public TopicExchange productExchange() {
        return new TopicExchange(ProductEventConstants.EXCHANGE_PRODUCT);
    }
}
