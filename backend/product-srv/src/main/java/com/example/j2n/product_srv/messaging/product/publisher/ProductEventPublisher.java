package com.example.j2n.product_srv.messaging.product.publisher;

import com.example.j2n.messaging.publisher.BaseEventPublisher;
import com.example.j2n.product_srv.messaging.product.constant.ProductEventConstants;
import com.example.j2n.product_srv.messaging.product.event.ProductCreatedEvent;
import com.example.j2n.product_srv.messaging.product.event.ProductDeletedEvent;
import com.example.j2n.product_srv.messaging.product.event.ProductUpdatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductEventPublisher extends BaseEventPublisher {

    public ProductEventPublisher(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
    }

    public void publishProductCreated(ProductCreatedEvent event) {
        publish(ProductEventConstants.EXCHANGE_PRODUCT, ProductEventConstants.RK_PRODUCT_CREATED, event);
    }

    public void publishProductUpdated(ProductUpdatedEvent event) {
        publish(ProductEventConstants.EXCHANGE_PRODUCT, ProductEventConstants.RK_PRODUCT_UPDATED, event);
    }

    public void publishProductDeleted(ProductDeletedEvent event) {
        publish(ProductEventConstants.EXCHANGE_PRODUCT, ProductEventConstants.RK_PRODUCT_DELETED, event);
    }
}
