package com.example.j2n.report_srv.messaging.product.config;

import com.example.j2n.report_srv.messaging.product.constant.ProductEventConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductEventConfig {

    @Bean
    public TopicExchange productExchange() {
        return new TopicExchange(ProductEventConstants.EXCHANGE_PRODUCT);
    }

    @Bean
    public Queue productQueue() {
        return QueueBuilder.durable(ProductEventConstants.QUEUE_REPORT_PRODUCT_TOUR).build();
    }

    @Bean
    public Binding productBinding() {
        return BindingBuilder
                .bind(productQueue())
                .to(productExchange())
                .with(ProductEventConstants.RK_PRODUCT_ALL);
    }
}
