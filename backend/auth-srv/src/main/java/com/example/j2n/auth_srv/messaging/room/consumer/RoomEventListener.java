package com.example.j2n.auth_srv.messaging.room.consumer;

import com.example.j2n.auth_srv.messaging.room.constant.RoomEventConstants;
import com.example.j2n.auth_srv.messaging.room.event.RoomMemberMappedEvent;
import com.example.j2n.auth_srv.messaging.room.event.RoomMemberRemovedEvent;
import com.example.j2n.auth_srv.service.UserService;
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
public class RoomEventListener {

    private final UserService userService;

    @RabbitListener(queues = RoomEventConstants.QUEUE_AUTH_ROOM_MEMBER_REMOVED)
    public void onRoomMemberRemoved(
            RoomMemberRemovedEvent event,
            Channel channel,
            Message message) throws IOException {

        try {
            log.info("[AUTH-SRV][EVENT] room.member.removed roomId={}, userId={}, roomNumber={}",
                    event.getRoomId(), event.getUserId(), event.getRoomNumber());
            userService.updateUserRoomId(event.getUserId(), null);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[AUTH-SRV][EVENT] Failed to process room member removed event", e);
            channel.basicNack(
                    message.getMessageProperties().getDeliveryTag(),
                    false,
                    false
            );
        }
    }

    @RabbitListener(queues = RoomEventConstants.QUEUE_AUTH_ROOM_MEMBER_MAPPED)
    public void onRoomMemberMapped(
            RoomMemberMappedEvent event,
            Channel channel,
            Message message) throws IOException {

        try {
            log.info("[AUTH-SRV][EVENT] room.member.mapped roomId={}, userIds={}",
                    event.getRoomId(), event.getUserIds());
            for (Long userId : event.getUserIds()) {
                userService.updateUserRoomId(userId, event.getRoomId());
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[AUTH-SRV][EVENT] Failed to process room member mapped event", e);
            channel.basicNack(
                    message.getMessageProperties().getDeliveryTag(),
                    false,
                    false
            );
        }
    }
}
