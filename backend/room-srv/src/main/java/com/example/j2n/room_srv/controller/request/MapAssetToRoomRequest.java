package com.example.j2n.room_srv.controller.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class MapAssetToRoomRequest {

    @NotNull(message = "Room ID is required")
    @Schema(description = "ID of the room", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long roomId;

    @NotNull(message = "Asset IDs are required")
    @Schema(description = "List of asset IDs to map", example = "[5, 6, 7]", requiredMode = Schema.RequiredMode.REQUIRED)
    private java.util.List<Long> assetIds;

}
