package com.example.j2n.travel_srv.messaging.travel.constant;

import lombok.Data;

@Data
public class TravelEventConstants {
    public static final String EXCHANGE_TRAVEL = "travel.exchange";
    // Queue
    public static final String QUEUE_TRAVEL_TOUR_IMAGE = "travel.tour_image.queue";
    // Routing key
    public static final String RK_TOUR_IMAGE_UPLOADED = "travel.tour_image.uploaded";
}
