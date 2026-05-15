package com.example.j2n.bff_srv.controller.request;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateRoomFeeRequest {
    @Schema(description = "List of fees to update for the room", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Fees list cannot be empty")
    @Valid
    private List<RoomFeeDto> fees;
}
