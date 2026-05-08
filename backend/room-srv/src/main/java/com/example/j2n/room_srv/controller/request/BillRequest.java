package com.example.j2n.room_srv.controller.request;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BillRequest {
    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Month is required")
    private Integer month;

    private Integer electricityNewIndex;
    private Integer electricityUsage;
    private Integer waterUsage;
    private java.math.BigDecimal otherServiceFees;
    private String renterId;
}
