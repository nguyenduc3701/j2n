package com.example.j2n.report_srv.messaging.travel.consumer;

import com.example.j2n.report_srv.messaging.travel.constant.TravelEventConstants;
import com.example.j2n.report_srv.messaging.travel.event.TourCreatedEvent;
import com.example.j2n.report_srv.messaging.travel.event.TourDeletedEvent;
import com.example.j2n.report_srv.service.ManagementService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TourEventListener {

    private final ManagementService managementService;

    @RabbitListener(queues = TravelEventConstants.QUEUE_REPORT_TRAVEL_TOUR)
    public void onTourCreated(
            TourCreatedEvent event,
            Channel channel,
            Message message) throws IOException {
        try {
            log.info("[REPORT-SRV][EVENT] tour.created event received tourId={}", event.getTourId());
            managementService.handleTourReport(true);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[REPORT-SRV][EVENT] Failed to process tour created event. Error: {}", e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    @RabbitListener(queues = TravelEventConstants.QUEUE_REPORT_TRAVEL_TOUR)
    public void onTourDeleted(
            TourDeletedEvent event,
            Channel channel,
            Message message) throws IOException {
        try {
            log.info("[REPORT-SRV][EVENT] tour.deleted event received tourId={}", event.getTourId());
            managementService.handleTourReport(false);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[REPORT-SRV][EVENT] Failed to process tour deleted event. Error: {}", e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
