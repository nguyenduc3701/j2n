package com.example.j2n.order_srv.messaging.order.publisher;

import com.example.j2n.order_srv.messaging.order.constant.OrderEventConstants;
import com.example.j2n.order_srv.messaging.order.event.OrderItemCreatedEvent;
import com.example.j2n.order_srv.messaging.order.event.OrderItemDeletedEvent;
import com.example.j2n.order_srv.messaging.order.event.OrderItemUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishOrderCreated(OrderItemCreatedEvent event) {
        log.info("[ORDER-SRV] Publishing order created event for item: {}", event.getItemId());
        rabbitTemplate.convertAndSend(OrderEventConstants.EXCHANGE_ORDER, OrderEventConstants.RK_ORDER_CREATED, event);
    }

    public void publishOrderUpdated(OrderItemUpdatedEvent event) {
        log.info("[ORDER-SRV] Publishing order updated event for item: {}", event.getItemId());
        rabbitTemplate.convertAndSend(OrderEventConstants.EXCHANGE_ORDER, OrderEventConstants.RK_ORDER_UPDATED, event);
    }

    public void publishOrderDeleted(OrderItemDeletedEvent event) {
        log.info("[ORDER-SRV] Publishing order deleted event for item: {}", event.getItemId());
        rabbitTemplate.convertAndSend(OrderEventConstants.EXCHANGE_ORDER, OrderEventConstants.RK_ORDER_DELETED, event);
    }
}
