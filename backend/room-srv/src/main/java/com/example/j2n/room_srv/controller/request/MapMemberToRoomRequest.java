package com.example.j2n.room_srv.controller.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Request object to map users to a specific room as members")
public class MapMemberToRoomRequest {

    @NotNull(message = "Room ID is required")
    @Schema(description = "ID of the room to map", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long roomId;

    @NotEmpty(message = "Users cannot be empty")
    @Schema(description = "List of User IDs to map as members", example = "[10, 11]", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> users;
}
