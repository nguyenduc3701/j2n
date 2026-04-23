package com.example.j2n.payment_srv.messaging.product.config;

import com.example.j2n.payment_srv.messaging.product.constant.ProductEventConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductEventConfig {

    @Bean
    public TopicExchange paymentProductExchange() {
        return new TopicExchange(ProductEventConstants.EXCHANGE_PRODUCT);
    }

    @Bean
    public Queue paymentProductSyncQueue() {
        return QueueBuilder.durable(ProductEventConstants.QUEUE_PAYMENT_PRODUCT_SYNC).build();
    }

    @Bean
    public Binding paymentProductBinding() {
        return BindingBuilder
                .bind(paymentProductSyncQueue())
                .to(paymentProductExchange())
                .with(ProductEventConstants.RK_PRODUCT_ALL);
    }
}
