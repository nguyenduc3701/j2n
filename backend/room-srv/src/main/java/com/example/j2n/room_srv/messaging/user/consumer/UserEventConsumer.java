package com.example.j2n.room_srv.messaging.user.consumer;

import com.example.j2n.room_srv.messaging.user.constant.UserEventConstants;
import com.example.j2n.room_srv.messaging.user.event.UserRegisteredEvent;
import com.example.j2n.room_srv.service.RoomMemberService;
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
public class UserEventConsumer {

    private final RoomMemberService roomMemberService;

    @RabbitListener(queues = UserEventConstants.QUEUE_ROOM_USER_REGISTERED)
    public void onUserRegistered(UserRegisteredEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("Received UserRegisteredEvent: {}", event);

            roomMemberService.handleUserRegisteredEvent(event);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("Failed to process UserRegisteredEvent: {}", e.getMessage(), e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
