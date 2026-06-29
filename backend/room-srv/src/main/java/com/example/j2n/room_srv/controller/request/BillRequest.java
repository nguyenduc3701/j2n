package com.example.j2n.room_srv.controller.request;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BillRequest {
    @Schema(description = "ID of the room", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Room ID is required")
    private Long roomId;

    @Schema(description = "Month for the bill (1-12), defaults to current month if null", example = "5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Builder.Default
    private Optional<Integer> month = Optional.empty();

    @Schema(description = "New electricity index", example = "1250", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "New electricity index is required")
    private Integer electricityNewIndex;

    @Schema(description = "ID of the renter", example = "100", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long renterId;
}
