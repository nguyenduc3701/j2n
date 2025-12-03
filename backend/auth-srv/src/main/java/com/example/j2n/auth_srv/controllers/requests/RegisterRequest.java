package com.example.j2n.auth_srv.controllers.requests;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(max = 50)
    private String userName;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255)
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100)
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String company;
    private Long roleId;
    private LocalDate birth;
}
