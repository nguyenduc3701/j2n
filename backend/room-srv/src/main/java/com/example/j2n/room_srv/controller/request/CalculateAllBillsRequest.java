package com.example.j2n.room_srv.controller.request;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CalculateAllBillsRequest {

    @Schema(description = "Billing month (1-12). Defaults to current month if null.",
            example = "5",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer month;

    @Schema(description = "Map of room ID to new electricity index for each occupied room.",
            example = "{\"1\": 1250, \"2\": 980}",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Electric indices map is required")
    private Map<Long, Integer> electricIndices;
}
