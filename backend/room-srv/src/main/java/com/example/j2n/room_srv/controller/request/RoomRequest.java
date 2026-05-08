package com.example.j2n.room_srv.controller.request;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class RoomRequest {
    @NotBlank(message = "Room number is required")
    private String roomNumber;

    private Integer floor;

    @NotNull(message = "Base price is required")
    private java.math.BigDecimal basePrice;

    private String area;

    private Integer totalPeople;

    private String status;

    private String description;
}
