package com.example.j2n.room_srv.controller.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AssetResponse {

    @Schema(description = "ID of the object", example = "1")
    private Long id;

    @Schema(description = "Name of the object", example = "Air Conditioner")
    private String name;

    @Schema(description = "Description of the object", example = "Daikin Inverter 1HP")
    private String description;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Update timestamp")
    private LocalDateTime updatedAt;
}
