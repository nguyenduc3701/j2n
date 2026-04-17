package com.example.j2n.image_srv.messaging.travel.publisher;

import com.example.j2n.image_srv.messaging.travel.constant.TravelEventConstants;
import com.example.j2n.image_srv.messaging.travel.event.TourImageUploadEvent;
import com.example.j2n.messaging.publisher.BaseEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class TravelEventPublisher extends BaseEventPublisher {

    public TravelEventPublisher(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
    }

    public void publishTourImageUploaded(TourImageUploadEvent event) {
        publish(TravelEventConstants.EXCHANGE_TRAVEL, TravelEventConstants.RK_TOUR_IMAGE_UPLOADED, event);
    }
}
