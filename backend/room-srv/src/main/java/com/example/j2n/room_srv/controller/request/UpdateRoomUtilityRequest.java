package com.example.j2n.room_srv.controller.request;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
public class UpdateRoomUtilityRequest {
    @NotEmpty(message = "Utility configs list cannot be empty")
    @Valid
    private List<RoomUtilityConfigDto> utilityConfigs;

    public List<RoomUtilityConfigDto> getUtilityConfigs() {
        return utilityConfigs;
    }
}
