package com.example.j2n.product_srv.messaging.product.consumer;

import com.example.j2n.product_srv.messaging.product.constant.ProductEventConstants;
import com.example.j2n.product_srv.messaging.product.event.ProductImageUploadEvent;
import com.example.j2n.product_srv.service.ProductImageService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductImageEventListener {

    private final ProductImageService productImageService;

    @RabbitListener(queues = ProductEventConstants.QUEUE_PRODUCT_IMAGE)
    public void onProductImageUploaded(
            ProductImageUploadEvent event,
            Channel channel,
            Message message) throws IOException {

        try {
            log.info("[PRODUCT-SRV][EVENT] Received product image upload event for productId={}, count={}",
                    event.getProductId(), event.getImages().size());

            productImageService.handleProductImageUploadEvent(event);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("[PRODUCT-SRV][EVENT] Successfully processed product image upload event for productId={}",
                    event.getProductId());
        } catch (Exception e) {
            log.error("[PRODUCT-SRV][EVENT] Failed to process product image upload event for productId={}",
                    event.getProductId(), e);
            // Re-queueing logic depends on business requirement. Here we send to DLQ
            // (false, false)
            channel.basicNack(
                    message.getMessageProperties().getDeliveryTag(),
                    false,
                    false);
        }
    }
}
