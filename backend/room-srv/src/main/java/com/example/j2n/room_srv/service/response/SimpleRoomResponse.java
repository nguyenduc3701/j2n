package com.example.j2n.room_srv.service.response;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SimpleRoomResponse {
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

    @Schema(description = "Indicates if the room has been soft-deleted", example = "false")
    private Boolean isDeleted;

    @Schema(description = "Indicates if the room is immutable (seed data, cannot be deleted by users)", example = "false")
    private Boolean isImmutable;
}
