package com.example.j2n.bff_srv.controller.request;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RegisterRequest {
    @Schema(description = "Username of the user", example = "newuser", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Username is required")
    @Size(max = 50)
    private String userName;

    @Schema(description = "Password of the user", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED, format = "password")
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255)
    private String password;

    @Schema(description = "Email of the user", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100)
    private String email;

    @Schema(description = "Full name of the user", example = "John Doe")
    private String fullName;

    @Schema(description = "Phone number of the user", example = "0987654321")
    private String phoneNumber;

    @Schema(description = "Address of the user", example = "123 Street, City")
    private String address;

    @Schema(description = "Company of the user", example = "Example Corp")
    private String company;

    @Schema(description = "Role ID assigned to the user", example = "1")
    private Long roleId;

    @Schema(description = "Birth date of the user", example = "1990-01-01")
    private LocalDate birth;
}
