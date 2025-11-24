package com.example.j2n.bff_srv.auth_srv.controllers.requests;

import com.example.j2n.bff_srv.auth_srv.repository.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {
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
    @NotBlank(message = "Role is required")
    private Long roleId;
    private User.Status status = User.Status.INACTIVE;
}
