package com.example.j2n.product_srv.messaging.product.config;

import com.example.j2n.product_srv.messaging.product.constant.ProductEventConstants;
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
    public Queue productImageQueue() {
        return QueueBuilder.durable(ProductEventConstants.QUEUE_PRODUCT_IMAGE).build();
    }

    @Bean
    public Binding productImageBinding() {
        return BindingBuilder
                .bind(productImageQueue())
                .to(productExchange())
                .with(ProductEventConstants.RK_PRODUCT_IMAGE_UPLOADED);
    }

    @Bean
    public Queue productLockQueue() {
        return QueueBuilder.durable(ProductEventConstants.QUEUE_PRODUCT_STOCK_LOCK).build();
    }

    @Bean
    public Binding productLockBinding() {
        return BindingBuilder
                .bind(productLockQueue())
                .to(productExchange())
                .with(ProductEventConstants.RK_PRODUCT_STOCK_LOCK);
    }

    @Bean
    public Queue productReleaseQueue() {
        return QueueBuilder.durable(ProductEventConstants.QUEUE_PRODUCT_STOCK_RELEASE).build();
    }

    @Bean
    public Binding productReleaseBinding() {
        return BindingBuilder
                .bind(productReleaseQueue())
                .to(productExchange())
                .with(ProductEventConstants.RK_PRODUCT_STOCK_RELEASE);
    }

    @Bean
    public Queue productConfirmQueue() {
        return QueueBuilder.durable(ProductEventConstants.QUEUE_PRODUCT_STOCK_CONFIRM).build();
    }

    @Bean
    public Binding productConfirmBinding() {
        return BindingBuilder
                .bind(productConfirmQueue())
                .to(productExchange())
                .with(ProductEventConstants.RK_PRODUCT_STOCK_CONFIRM);
    }
}
