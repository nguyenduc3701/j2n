package com.example.j2n.report_srv.messaging.user.constant;

import lombok.Data;

@Data
public class UserEventConstants {
    public static final String EXCHANGE_USER = "user.exchange";
    public static final String QUEUE_AUTH_AVATAR = "auth.user.avatar.queue";
    public static final String RK_AVATAR_UPLOADED = "user.avatar.uploaded";
}
