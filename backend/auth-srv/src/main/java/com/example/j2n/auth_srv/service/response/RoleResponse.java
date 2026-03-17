package com.example.j2n.auth_srv.service.response;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class RoleResponse {
    @Schema(example = "1")
    private Long id;
    @Schema(example = "ROLE_ADMIN")
    private String name;
    @Schema(example = "Administrator role")
    private String description;
}
