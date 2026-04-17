package com.example.j2n.report_srv.messaging.travel.constant;

import lombok.Data;

@Data
public class TravelEventConstants {
    public static final String EXCHANGE_TRAVEL = "travel.exchange";
    // Queue
    public static final String QUEUE_REPORT_TRAVEL_TOUR = "report.travel.tour.queue";
    // Routing key
    public static final String RK_TOUR_CREATED = "travel.tour.created";
    public static final String RK_TOUR_DELETED = "travel.tour.deleted";
    public static final String RK_TOUR_ALL = "travel.#";
}
