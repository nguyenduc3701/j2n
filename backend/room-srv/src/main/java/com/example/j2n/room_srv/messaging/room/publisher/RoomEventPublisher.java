package com.example.j2n.room_srv.messaging.room.publisher;

import com.example.j2n.room_srv.messaging.room.constant.RoomEventConstants;
import com.example.j2n.room_srv.messaging.room.event.BillsCalculatedEvent;
import com.example.j2n.room_srv.messaging.room.event.RoomStatusUpdatedEvent;
import com.example.j2n.room_srv.messaging.report.event.RoomRevenueEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishRoomStatusUpdated(RoomStatusUpdatedEvent event) {
        log.info("Publishing room status updated event: {}", event);
        rabbitTemplate.convertAndSend(
                RoomEventConstants.EXCHANGE_ROOM,
                RoomEventConstants.RK_ROOM_STATUS_UPDATED,
                event
        );
    }

    public void publishBillsCalculated(BillsCalculatedEvent event) {
        log.info("Publishing bills calculated event for month-year {}: {}", event.getMonthYear(), event);
        rabbitTemplate.convertAndSend(
                RoomEventConstants.EXCHANGE_ROOM,
                RoomEventConstants.RK_BILLS_CALCULATED,
                event
        );
    }

    public void publishRoomRevenue(RoomRevenueEvent event) {
        log.info("Publishing room revenue event for room {}: {}", event.getRoomNumber(), event);
        rabbitTemplate.convertAndSend(
                RoomEventConstants.EXCHANGE_ROOM,
                RoomEventConstants.RK_ROOM_REVENUE,
                event
        );
    }
}
