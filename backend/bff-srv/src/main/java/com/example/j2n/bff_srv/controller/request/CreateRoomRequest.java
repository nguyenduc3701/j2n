package com.example.j2n.bff_srv.controller.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class CreateRoomRequest {

    @NotBlank(message = "Room number is required")
    @Schema(description = "Unique room number", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roomNumber;

    @Schema(description = "Floor number", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer floor;

    @NotNull(message = "Base price is required")
    @Schema(description = "Base monthly price", example = "2000000", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal basePrice;

    @Schema(description = "Room area (e.g. 20m2)", example = "20m2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String area;

    @Schema(description = "Maximum occupancy", example = "2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer maxPeople;

    @Schema(description = "Current room status", example = "AVAILABLE", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String status;

    @Schema(description = "Current electricity meter index", example = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer currentElectricIndex;

    @Schema(description = "Detailed room description", example = "Spacious room with window", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;
}
