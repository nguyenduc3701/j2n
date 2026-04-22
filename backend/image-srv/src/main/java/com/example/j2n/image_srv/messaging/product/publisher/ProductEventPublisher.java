package com.example.j2n.image_srv.messaging.product.publisher;

import com.example.j2n.image_srv.messaging.product.constant.ProductEventConstants;
import com.example.j2n.image_srv.messaging.product.event.ProductImageUploadEvent;
import com.example.j2n.messaging.publisher.BaseEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductEventPublisher extends BaseEventPublisher {

    public ProductEventPublisher(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
    }

    public void publishProductImageUploaded(ProductImageUploadEvent event) {
        publish(ProductEventConstants.EXCHANGE_PRODUCT, ProductEventConstants.RK_TOUR_IMAGE_UPLOADED, event);
    }
}
