package com.example.j2n.report_srv.messaging.product.consumer;

import com.example.j2n.report_srv.messaging.product.constant.TravelEventConstants;
import com.example.j2n.report_srv.messaging.product.event.ProductCreatedEvent;
import com.example.j2n.report_srv.messaging.product.event.ProductDeletedEvent;
import com.example.j2n.report_srv.service.ManagementService;
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
public class ProductEventListener {

    private final ManagementService managementService;

    @RabbitListener(queues = TravelEventConstants.QUEUE_REPORT_PRODUCT_TOUR)
    public void onProductCreated(
            ProductCreatedEvent event,
            Channel channel,
            Message message) throws IOException {
        try {
            log.info("[REPORT-SRV][EVENT] product.created event received productId={}", event.getProductId());
            managementService.handleProductReport(true);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[REPORT-SRV][EVENT] Failed to process product created event. Error: {}", e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    @RabbitListener(queues = TravelEventConstants.QUEUE_REPORT_PRODUCT_TOUR)
    public void onProductDeleted(
            ProductDeletedEvent event,
            Channel channel,
            Message message) throws IOException {
        try {
            log.info("[REPORT-SRV][EVENT] product.deleted event received productId={}", event.getProductId());
            managementService.handleProductReport(false);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[REPORT-SRV][EVENT] Failed to process product deleted event. Error: {}", e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
