package com.example.j2n.auth_srv.service.response;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class PermissionResponse {
    @Schema(example = "1")
    private Long id;
    @Schema(example = "CAN_VIEW_USER")
    private String name;
    @Schema(example = "Permission to view user details")
    private String description;
}
