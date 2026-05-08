package com.example.j2n.order_srv.messaging.room.consumer;

import com.example.j2n.order_srv.messaging.room.constant.RoomEventConstants;
import com.example.j2n.order_srv.messaging.room.event.RoomBillSyncedEvent;
import com.example.j2n.order_srv.service.OrderService;
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
public class RoomEventConsumer {

    private final OrderService orderService;

    @RabbitListener(queues = RoomEventConstants.QUEUE_ORDER_ROOM_SYNC)
    public void handleRoomBillSynced(RoomBillSyncedEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("[ORDER-SRV] Received room bill synced event: {}", event.getBillId());
            orderService.syncRoomBill(event);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[ORDER-SRV] Failed to process room bill synced event", e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
