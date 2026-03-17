package com.example.j2n.auth_srv.controllers.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AssignRoleRequest {
    @Schema(description = "Role ID to be assigned", example = "1")
    private String roleId;
}
