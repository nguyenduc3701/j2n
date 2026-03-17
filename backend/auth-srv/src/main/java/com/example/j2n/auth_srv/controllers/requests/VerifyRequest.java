package com.example.j2n.auth_srv.controllers.requests;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VerifyRequest {
    @Schema(description = "User ID to verify", example = "1")
    private String userId;

    @Schema(description = "Role ID of the user", example = "1")
    private String roleId;

    @Schema(description = "List of permissions of the user", example = "[\"CAN_VIEW_MANAGEMENT_PAGE\"]")
    private List<String> permissions;
}
