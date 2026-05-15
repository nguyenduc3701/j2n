package com.example.j2n.report_srv.messaging.room.constant;

import lombok.Data;

@Data
public class RoomEventConstants {
    public static final String EXCHANGE_ROOM = "room.exchange";

    // Queue names
    public static final String QUEUE_REPORT_ROOM_STATUS_UPDATED = "report.room.status.updated.queue";
    public static final String QUEUE_REPORT_BILLS_CALCULATED = "report.room.bills_calculated.queue";

    // Routing keys
    public static final String RK_ROOM_STATUS_UPDATED = "room.status.updated";
    public static final String RK_BILLS_CALCULATED = "room.bills_calculated";
}
