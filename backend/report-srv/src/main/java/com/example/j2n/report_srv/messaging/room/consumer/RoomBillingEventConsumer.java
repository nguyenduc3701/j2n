package com.example.j2n.report_srv.messaging.room.consumer;

import com.example.j2n.report_srv.messaging.room.constant.RoomEventConstants;
import com.example.j2n.report_srv.messaging.room.event.BillsCalculatedEvent;
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
public class RoomBillingEventConsumer {

    private final ManagementService managementService;

    @RabbitListener(queues = RoomEventConstants.QUEUE_REPORT_BILLS_CALCULATED)
    public void onBillsCalculated(BillsCalculatedEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("Received bills calculated event: {}", event);

            // DELEGATE TO SERVICE
            managementService.handleBillsCalculated(event);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("Failed to process bills calculated event: {}", e.getMessage(), e);
            // nack(deliveryTag, multiple, requeue)
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
