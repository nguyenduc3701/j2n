package com.example.j2n.report_srv.messaging.user.constant;

import lombok.Data;

@Data
public class UserEventConstants {
    public static final String EXCHANGE_USER = "user.exchange";
    // Queue
    public static final String QUEUE_AUTH_REGISTERED = "auth.user.registered.queue";
    // Routing key
    public static final String RK_USER_REGISTERED = "user.registered";
}
