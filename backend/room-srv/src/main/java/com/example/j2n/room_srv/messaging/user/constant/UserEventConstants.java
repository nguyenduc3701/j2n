package com.example.j2n.room_srv.messaging.user.constant;

import lombok.Data;

@Data
public class UserEventConstants {
    public static final String EXCHANGE_USER = "user.exchange";
    public static final String QUEUE_ROOM_USER_REGISTERED = "room.user.registered.queue";
    public static final String QUEUE_ROOM_USER_UPDATED = "room.user.updated.queue";
    public static final String QUEUE_ROOM_USER_DELETED = "room.user.deleted.queue";
    
    public static final String RK_USER_REGISTERED = "user.registered";
    public static final String RK_USER_UPDATED = "user.updated";
    public static final String RK_USER_DELETED = "user.deleted";
}
