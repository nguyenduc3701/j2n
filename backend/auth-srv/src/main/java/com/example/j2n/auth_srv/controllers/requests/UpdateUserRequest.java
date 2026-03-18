package com.example.j2n.auth_srv.controllers.requests;

import com.example.j2n.auth_srv.repository.entity.UserEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateUserRequest {

    @Schema(description = "Full name of the user", example = "John Doe")
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String fullName;

    @Schema(description = "Phone number of the user", example = "0987654321")
    private String phoneNumber;

    @Schema(description = "Address of the user", example = "123 Street, City")
    private String address;

    @Schema(description = "Company of the user", example = "Example Corp")
    private String company;

    @Schema(description = "Birth date of the user", example = "1990-01-01")
    private LocalDate birth;

    @Schema(description = "Status of the user", example = "ACTIVE")
    private UserEntity.Status status;

    @Schema(description = "Role ID assigned to the user", example = "3")
    private Long roleId;

    @Schema(description = "Image URL of the user", example = "/api/bff/image/user/some-image-id")
    private String imageUrl;

    @Schema(description = "Room ID assigned to the user", example = "101")
    private Long roomId;
}