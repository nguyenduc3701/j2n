package com.example.j2n.payment_srv.messaging.order.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderItemUpdatedEvent {
    private Long id;
    private String userId;
    private String itemId;
    private String itemType;
    private Integer quantity;
    private String size;
    private String design;
}
