package com.example.j2n.auth_srv.controllers.requests;

import com.example.j2n.auth_srv.repository.entity.User;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)   
public class UpdateUserRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100)
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String company;
    private User.Status status = User.Status.INACTIVE;
}
