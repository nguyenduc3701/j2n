package com.example.j2n.auth_srv.controllers.requests;

import java.time.LocalDate;

import com.example.j2n.auth_srv.repository.entity.UserEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateUserRequest {
    private String fullName;
    private String phoneNumber;
    private String address;
    private String company;
    private Long roleId;
    private Long roomId;
    private LocalDate birth;
    private UserEntity.Status status;
}
