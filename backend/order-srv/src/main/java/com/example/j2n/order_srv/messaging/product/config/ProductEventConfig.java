package com.example.j2n.order_srv.messaging.product.config;

import com.example.j2n.order_srv.messaging.product.constant.ProductEventConstants;
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
    public Queue productSyncQueue() {
        return new Queue(ProductEventConstants.QUEUE_ORDER_PRODUCT_SYNC);
    }

    @Bean
    public Binding bindingCreated(Queue productSyncQueue, TopicExchange productExchange) {
        return BindingBuilder.bind(productSyncQueue).to(productExchange).with(ProductEventConstants.RK_PRODUCT_CREATED);
    }

    @Bean
    public Binding bindingUpdated(Queue productSyncQueue, TopicExchange productExchange) {
        return BindingBuilder.bind(productSyncQueue).to(productExchange).with(ProductEventConstants.RK_PRODUCT_UPDATED);
    }

    @Bean
    public Binding bindingDeleted(Queue productSyncQueue, TopicExchange productExchange) {
        return BindingBuilder.bind(productSyncQueue).to(productExchange).with(ProductEventConstants.RK_PRODUCT_DELETED);
    }
}
