package com.example.j2n.room_srv.messaging.user.constant;

import lombok.Data;

@Data
public class UserEventConstants {
    public static final String EXCHANGE_USER = "user.exchange";
    public static final String QUEUE_ROOM_USER_REGISTERED = "room.user.registered.queue";
    public static final String RK_USER_REGISTERED = "user.registered";
}
