package com.example.j2n.order_srv.messaging.product.consumer;

import com.example.j2n.order_srv.messaging.product.constant.ProductEventConstants;
import com.example.j2n.order_srv.messaging.product.event.ProductCreatedEvent;
import com.example.j2n.order_srv.messaging.product.event.ProductDeletedEvent;
import com.example.j2n.order_srv.messaging.product.event.ProductUpdatedEvent;
import com.example.j2n.order_srv.service.CartService;
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

    private final CartService cartService;

    @RabbitListener(queues = ProductEventConstants.QUEUE_ORDER_PRODUCT_SYNC)
    public void handleProductCreated(ProductCreatedEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("[ORDER-SRV] Received product created event: {}", event.getProductId());
            cartService.syncProductCreated(event);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[ORDER-SRV] Failed to process product created event", e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    @RabbitListener(queues = ProductEventConstants.QUEUE_ORDER_PRODUCT_SYNC)
    public void handleProductUpdated(ProductUpdatedEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("[ORDER-SRV] Received product updated event: {}", event.getProductId());
            cartService.syncProductUpdated(event);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[ORDER-SRV] Failed to process product updated event", e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    @RabbitListener(queues = ProductEventConstants.QUEUE_ORDER_PRODUCT_SYNC)
    public void handleProductDeleted(ProductDeletedEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("[ORDER-SRV] Received product deleted event: {}", event.getProductId());
            cartService.syncProductDeleted(event);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[ORDER-SRV] Failed to process product deleted event", e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
