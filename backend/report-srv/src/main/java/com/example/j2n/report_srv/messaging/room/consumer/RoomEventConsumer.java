package com.example.j2n.report_srv.messaging.room.consumer;

import com.example.j2n.report_srv.messaging.room.constant.RoomEventConstants;
import com.example.j2n.report_srv.messaging.room.event.RoomStatusUpdatedEvent;
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
public class RoomEventConsumer {

    private final ManagementService managementService;

    @RabbitListener(queues = RoomEventConstants.QUEUE_REPORT_ROOM_STATUS_UPDATED)
    public void onRoomStatusUpdated(RoomStatusUpdatedEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("Received room status updated event: {}", event);

            managementService.handleRoomStatusUpdate(event);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("Failed to process room status updated event: {}", e.getMessage(), e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
