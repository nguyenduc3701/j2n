package com.example.j2n.auth_srv.controllers.requests;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VerifyRequest {
    private String userId;
    private String roleId;
    private List<String> permissions;
}
