package com.example.j2n.bff_srv.auth_srv.controllers.requests;

import com.example.j2n.bff_srv.auth_srv.repository.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String company;
    private User.Status status = User.Status.INACTIVE;
}
