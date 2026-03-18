package com.example.j2n.auth_srv.controllers.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateUserRequest {

    @Schema(description = "Username of the user", example = "newuser", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Username is required")
    @Size(max = 50, message = "Username must not exceed 50 characters")
    private String userName;

    @Schema(description = "Password of the user", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED, format = "password")
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    private String password;

    @Schema(description = "Email of the user", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Schema(description = "Full name of the user", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Full name is required")
    private String fullName;

    @Schema(description = "Phone number of the user", example = "0987654321")
    private String phoneNumber;

    @Schema(description = "Address of the user", example = "123 Street, City")
    private String address;

    @Schema(description = "Company of the user", example = "Example Corp")
    private String company;

    @Schema(description = "Role ID assigned to the user", example = "3")
    private Long roleId;

    @Schema(description = "Birth date of the user", example = "1990-01-01")
    private LocalDate birth;

    @Schema(description = "Room ID assigned to the user", example = "101")
    private Long roomId;
}