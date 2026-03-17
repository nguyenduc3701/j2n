package com.example.j2n.auth_srv.controllers.requests;

import java.time.LocalDate;

import com.example.j2n.auth_srv.repository.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateUserRequest {
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

    @Schema(description = "Role ID assigned to the user", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Role is required")
    private Long roleId;

    @Schema(description = "Room ID assigned to the user", example = "101")
    private Long roomId;

    @Schema(description = "Birth date of the user", example = "1990-01-01")
    private LocalDate birth;

    @Schema(description = "Initial status of the user", example = "INACTIVE")
    private UserEntity.Status status = UserEntity.Status.INACTIVE;
}
