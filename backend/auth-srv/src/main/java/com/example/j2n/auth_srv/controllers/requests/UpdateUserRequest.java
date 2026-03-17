package com.example.j2n.auth_srv.controllers.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

import com.example.j2n.auth_srv.repository.entity.UserEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateUserRequest {
    @Schema(description = "Full name of the user", example = "John Doe Update")
    private String fullName;

    @Schema(description = "Phone number of the user", example = "0123456789")
    private String phoneNumber;

    @Schema(description = "Address of the user", example = "456 Avenue, City")
    private String address;

    @Schema(description = "Company of the user", example = "Update Corp")
    private String company;

    @Schema(description = "Role ID assigned to the user", example = "2")
    private Long roleId;

    @Schema(description = "Room ID assigned to the user", example = "202")
    private Long roomId;

    @Schema(description = "Birth date of the user", example = "1990-01-01")
    private LocalDate birth;

    @Schema(description = "Status of the user", example = "ACTIVE")
    private UserEntity.Status status;

    @Schema(description = "Profile image URL of the user", example = "http://example.com/image.png")
    private String imageUrl;
}
