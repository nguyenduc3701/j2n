package com.example.j2n.room_srv.controller.response;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RoomResponse {
    @Schema(description = "Room ID", example = "1")
    private Long id;

    @Schema(description = "Room number", example = "101")
    private String roomNumber;

    @Schema(description = "Floor number", example = "1")
    private Integer floor;

    @Schema(description = "Base monthly price", example = "2000000")
    private BigDecimal basePrice;

    @Schema(description = "Room area", example = "20m2")
    private String area;

    @Schema(description = "Maximum occupancy", example = "2")
    private Integer maxPeople;

    @Schema(description = "Room status", example = "AVAILABLE")
    private String status;

    @Schema(description = "Current electricity meter index", example = "150")
    private Integer currentElectricIndex;

    @Schema(description = "Room description", example = "Spacious room")
    private String description;

    @Schema(description = "Creation timestamp", example = "2024-05-11T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-05-11T10:00:00")
    private LocalDateTime updatedAt;
}
