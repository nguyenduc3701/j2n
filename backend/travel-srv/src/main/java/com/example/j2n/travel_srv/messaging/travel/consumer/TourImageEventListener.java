package com.example.j2n.travel_srv.messaging.travel.consumer;

import com.example.j2n.travel_srv.messaging.travel.constant.TravelEventConstants;
import com.example.j2n.travel_srv.messaging.travel.event.TourImageUploadEvent;
import com.example.j2n.travel_srv.service.TourImageService;
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
public class TourImageEventListener {

    private final TourImageService tourImageService;

    @RabbitListener(queues = TravelEventConstants.QUEUE_TRAVEL_TOUR_IMAGE)
    public void onTourImageUploaded(
            TourImageUploadEvent event,
            Channel channel,
            Message message) throws IOException {

        try {
            log.info("[TRAVEL-SRV][EVENT] Received tour image upload event for tourId={}, count={}",
                    event.getTourId(), event.getImages().size());
            
            tourImageService.handleTourImageUploadEvent(event);
            
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("[TRAVEL-SRV][EVENT] Successfully processed tour image upload event for tourId={}", event.getTourId());
        } catch (Exception e) {
            log.error("[TRAVEL-SRV][EVENT] Failed to process tour image upload event for tourId={}", event.getTourId(), e);
            // Re-queueing logic depends on business requirement. Here we send to DLQ (false, false)
            channel.basicNack(
                    message.getMessageProperties().getDeliveryTag(),
                    false,
                    false
            );
        }
    }
}
