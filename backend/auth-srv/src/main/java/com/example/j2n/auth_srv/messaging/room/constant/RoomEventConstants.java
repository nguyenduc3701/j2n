package com.example.j2n.auth_srv.messaging.room.constant;

public class RoomEventConstants {
    public static final String EXCHANGE_ROOM = "room.exchange";

    // Queue names (unique per service-queue pair)
    public static final String QUEUE_AUTH_ROOM_MEMBER_REMOVED = "auth.room.member_removed.queue";
    public static final String QUEUE_AUTH_ROOM_MEMBER_MAPPED = "auth.room.member_mapped.queue";

    // Routing keys
    public static final String RK_ROOM_MEMBER_REMOVED = "room.member.removed";
    public static final String RK_ROOM_MEMBER_MAPPED = "room.member.mapped";
}
