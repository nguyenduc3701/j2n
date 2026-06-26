package com.example.j2n.auth_srv.messaging.room.consumer;

import static org.mockito.Mockito.*;

import com.example.j2n.auth_srv.messaging.room.event.RoomMemberRemovedEvent;
import com.example.j2n.auth_srv.service.UserService;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class RoomEventListenerTest {

    @Mock
    private UserService userService;

    @Mock
    private Channel channel;

    @Mock
    private Message message;

    @Mock
    private MessageProperties messageProperties;

    @InjectMocks
    private RoomEventListener roomEventListener;

    @Test
    void onRoomMemberRemoved_Success() throws IOException {
        RoomMemberRemovedEvent event = RoomMemberRemovedEvent.builder()
                .roomId(10L)
                .userId(1L)
                .roomNumber("Room 101")
                .build();

        long deliveryTag = 123L;
        when(message.getMessageProperties()).thenReturn(messageProperties);
        when(messageProperties.getDeliveryTag()).thenReturn(deliveryTag);

        roomEventListener.onRoomMemberRemoved(event, channel, message);

        verify(userService).updateUserRoomId(1L, null);
        verify(channel).basicAck(deliveryTag, false);
        verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
    }

    @Test
    void onRoomMemberRemoved_Failure_Nack() throws IOException {
        RoomMemberRemovedEvent event = RoomMemberRemovedEvent.builder()
                .roomId(10L)
                .userId(1L)
                .roomNumber("Room 101")
                .build();

        long deliveryTag = 123L;
        when(message.getMessageProperties()).thenReturn(messageProperties);
        when(messageProperties.getDeliveryTag()).thenReturn(deliveryTag);
        doThrow(new RuntimeException("Database error")).when(userService).updateUserRoomId(1L, null);

        roomEventListener.onRoomMemberRemoved(event, channel, message);

        verify(userService).updateUserRoomId(1L, null);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
        verify(channel).basicNack(deliveryTag, false, false);
    }
}
