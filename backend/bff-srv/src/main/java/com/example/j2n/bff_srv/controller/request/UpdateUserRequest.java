package com.example.j2n.bff_srv.controller.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private Boolean enabled;
    private Boolean emailVerified;
}
