package com.example.j2n.product_srv.messaging.product.consumer;

import com.example.j2n.product_srv.messaging.product.constant.ProductEventConstants;
import com.example.j2n.product_srv.messaging.product.event.ConfirmProductStockEvent;
import com.example.j2n.product_srv.messaging.product.event.LockProductStockEvent;
import com.example.j2n.product_srv.messaging.product.event.ReleaseProductStockEvent;
import com.example.j2n.product_srv.service.ProductService;
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
public class ProductStockEventConsumer {

    private final ProductService productService;

    @RabbitListener(queues = ProductEventConstants.QUEUE_PRODUCT_STOCK_LOCK)
    public void handleLockStock(LockProductStockEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("[PRODUCT-SRV] Lock stock received for transaction: {}", event.getTransactionId());
            event.getItems().forEach(item -> 
                productService.lockStock(Long.valueOf(item.getProductId()), item.getQuantity())
            );
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[PRODUCT-SRV] Failed to lock stock", e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    @RabbitListener(queues = ProductEventConstants.QUEUE_PRODUCT_STOCK_RELEASE)
    public void handleReleaseStock(ReleaseProductStockEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("[PRODUCT-SRV] Release stock received for transaction: {}", event.getTransactionId());
            event.getItems().forEach(item -> 
                productService.releaseStock(Long.valueOf(item.getProductId()), item.getQuantity())
            );
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[PRODUCT-SRV] Failed to release stock", e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    @RabbitListener(queues = ProductEventConstants.QUEUE_PRODUCT_STOCK_CONFIRM)
    public void handleConfirmStock(ConfirmProductStockEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("[PRODUCT-SRV] Confirm stock received for transaction: {}", event.getTransactionId());
            event.getItems().forEach(item -> 
                productService.confirmStock(Long.valueOf(item.getProductId()), item.getQuantity())
            );
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[PRODUCT-SRV] Failed to confirm stock", e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
