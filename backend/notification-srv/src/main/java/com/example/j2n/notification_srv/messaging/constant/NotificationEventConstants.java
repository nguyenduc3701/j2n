package com.example.j2n.notification_srv.messaging.constant;

public class NotificationEventConstants {

    // Exchange - reuse the room exchange since notification is consumer
    public static final String EXCHANGE_ROOM = "room.exchange";

    // Queue names
    public static final String QUEUE_NOTIFICATION_BILL = "notification.bill.queue";

    // Routing keys
    public static final String RK_BILL_NOTIFICATION = "room.bill.notification";
}
