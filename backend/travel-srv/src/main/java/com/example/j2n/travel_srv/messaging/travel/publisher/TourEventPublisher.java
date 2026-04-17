package com.example.j2n.travel_srv.messaging.travel.publisher;

import com.example.j2n.messaging.publisher.BaseEventPublisher;
import com.example.j2n.travel_srv.messaging.travel.constant.TravelEventConstants;
import com.example.j2n.travel_srv.messaging.travel.event.TourCreatedEvent;
import com.example.j2n.travel_srv.messaging.travel.event.TourDeletedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class TourEventPublisher extends BaseEventPublisher {

    public TourEventPublisher(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
    }

    public void publishTourCreated(TourCreatedEvent event) {
        publish(TravelEventConstants.EXCHANGE_TRAVEL, TravelEventConstants.RK_TOUR_CREATED, event);
    }

    public void publishTourDeleted(TourDeletedEvent event) {
        publish(TravelEventConstants.EXCHANGE_TRAVEL, TravelEventConstants.RK_TOUR_DELETED, event);
    }
}
