package com.example.j2n.payment_srv.messaging.order.consumer;

import com.example.j2n.payment_srv.messaging.order.constant.OrderEventConstants;
import com.example.j2n.payment_srv.messaging.order.event.OrderItemCreatedEvent;
import com.example.j2n.payment_srv.messaging.order.event.OrderItemDeletedEvent;
import com.example.j2n.payment_srv.messaging.order.event.OrderItemUpdatedEvent;
import com.example.j2n.payment_srv.service.OrderInfoService;
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
public class OrderEventConsumer {

    private final OrderInfoService orderInfoService;

    @RabbitListener(queues = OrderEventConstants.QUEUE_PAYMENT_ORDER_SYNC)
    public void handleOrderCreated(OrderItemCreatedEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("[PAYMENT-SRV] Order item created event received: userId={}, itemId={}", event.getUserId(), event.getItemId());
            orderInfoService.syncOrderCreated(event);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[PAYMENT-SRV] Failed to sync order created for user: {}", event.getUserId(), e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    @RabbitListener(queues = OrderEventConstants.QUEUE_PAYMENT_ORDER_SYNC)
    public void handleOrderUpdated(OrderItemUpdatedEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("[PAYMENT-SRV] Order item updated event received: userId={}, itemId={}", event.getUserId(), event.getItemId());
            orderInfoService.syncOrderUpdated(event);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[PAYMENT-SRV] Failed to sync order updated for user: {}", event.getUserId(), e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    @RabbitListener(queues = OrderEventConstants.QUEUE_PAYMENT_ORDER_SYNC)
    public void handleOrderDeleted(OrderItemDeletedEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("[PAYMENT-SRV] Order item deleted event received: userId={}, itemId={}", event.getUserId(), event.getItemId());
            orderInfoService.syncOrderDeleted(event.getUserId(), event.getItemId(), event.getItemType());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[PAYMENT-SRV] Failed to sync order deleted for user: {}", event.getUserId(), e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
