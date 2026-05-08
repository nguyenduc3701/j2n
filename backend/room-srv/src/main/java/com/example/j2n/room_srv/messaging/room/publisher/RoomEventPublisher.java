package com.example.j2n.room_srv.messaging.room.publisher;

import com.example.j2n.messaging.publisher.BaseEventPublisher;
import com.example.j2n.room_srv.messaging.room.constant.RoomEventConstants;
import com.example.j2n.room_srv.messaging.room.event.RoomBillSyncedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RoomEventPublisher extends BaseEventPublisher {

    public RoomEventPublisher(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
    }

    public void publishRoomBillSynced(RoomBillSyncedEvent event) {
        publish(RoomEventConstants.EXCHANGE_ROOM, RoomEventConstants.RK_ROOM_BILL_SYNCED, event);
    }

    public void publishRoomRevenue(com.example.j2n.room_srv.messaging.report.event.RoomRevenueEvent event) {
        publish(RoomEventConstants.EXCHANGE_ROOM, "room.revenue.reported", event);
    }
}
