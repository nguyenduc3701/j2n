package com.example.j2n.payment_srv.messaging.product.consumer;

import com.example.j2n.payment_srv.messaging.product.constant.ProductEventConstants;
import com.example.j2n.payment_srv.messaging.product.event.ProductCreatedEvent;
import com.example.j2n.payment_srv.messaging.product.event.ProductDeletedEvent;
import com.example.j2n.payment_srv.messaging.product.event.ProductUpdatedEvent;
import com.example.j2n.payment_srv.service.ProductInfoService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductEventConsumer {

    private final ProductInfoService productInfoService;

    @RabbitListener(queues = ProductEventConstants.QUEUE_PAYMENT_PRODUCT_SYNC)
    public void handleProductCreated(ProductCreatedEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("[PAYMENT-SRV] Product created event received: {}", event.getProductId());
            productInfoService.syncProductCreated(event);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[PAYMENT-SRV] Failed to sync product created: {}", event.getProductId(), e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    @RabbitListener(queues = ProductEventConstants.QUEUE_PAYMENT_PRODUCT_SYNC)
    public void handleProductUpdated(ProductUpdatedEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("[PAYMENT-SRV] Product updated event received: {}", event.getProductId());
            productInfoService.syncProductUpdated(event);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[PAYMENT-SRV] Failed to sync product updated: {}", event.getProductId(), e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    @RabbitListener(queues = ProductEventConstants.QUEUE_PAYMENT_PRODUCT_SYNC)
    public void handleProductDeleted(ProductDeletedEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("[PAYMENT-SRV] Product deleted event received: {}", event.getProductId());
            productInfoService.syncProductDeleted(event.getProductId());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[PAYMENT-SRV] Failed to sync product deleted: {}", event.getProductId(), e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
