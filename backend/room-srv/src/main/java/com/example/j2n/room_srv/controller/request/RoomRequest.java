package com.example.j2n.room_srv.controller.request;

import com.example.j2n.validation.ValidNotEmpty;
import com.example.j2n.validation.ValidatableRequest;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@ValidNotEmpty
public class RoomRequest implements ValidatableRequest {
    @Schema(description = "Unique room number", example = "101", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Builder.Default
    private Optional<String> roomNumber = Optional.empty();

    @Schema(description = "Floor number", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Builder.Default
    private Optional<Integer> floor = Optional.empty();

    @Schema(description = "Base monthly price", example = "2000000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Builder.Default
    private Optional<BigDecimal> basePrice = Optional.empty();

    @Schema(description = "Room area (e.g. 20m2)", example = "20m2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Builder.Default
    private Optional<String> area = Optional.empty();

    @Schema(description = "Maximum occupancy", example = "2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Builder.Default
    private Optional<Integer> maxPeople = Optional.empty();

    @Schema(description = "Current room status", example = "AVAILABLE", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Builder.Default
    private Optional<String> status = Optional.empty();

    @Schema(description = "Current electricity meter index", example = "150", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Builder.Default
    private Optional<Integer> currentElectricIndex = Optional.empty();

    @Schema(description = "Detailed room description", example = "Spacious room with window", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Builder.Default
    private Optional<String> description = Optional.empty();

    @Override
    public boolean isEmpty() {
        return roomNumber.isEmpty() && floor.isEmpty() && basePrice.isEmpty() 
            && area.isEmpty() && maxPeople.isEmpty() && status.isEmpty() 
            && currentElectricIndex.isEmpty() && description.isEmpty();
    }
}
