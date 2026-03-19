package com.example.j2n.bff_srv.controller.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LogoutRequest {
    @NotBlank(message = "Refresh token is required")
    @Schema(description = "Refresh token to get new access token", example = "ey...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;
}
