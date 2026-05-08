package com.example.j2n.room_srv.controller.response;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RoomUtilityResponse {
    private Long id;
    private Long utilityConfigId;
    private String name;
    private String type;
    private BigDecimal unitPrice;
    private String unitName;
    private Integer quantity;
}
