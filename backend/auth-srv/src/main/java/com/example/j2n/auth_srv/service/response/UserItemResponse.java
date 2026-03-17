package com.example.j2n.auth_srv.service.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserItemResponse {
    @Schema(example = "1")
    private String id;
    @Schema(example = "admin")
    private String userName;
    @Schema(example = "admin@example.com")
    private String email;
    @Schema(example = "2024-03-17T09:05:47Z")
    private String createdAt;
}
