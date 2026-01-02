package com.example.j2n.bff_srv.controller.request;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private Boolean enabled;
    private Boolean emailVerified;
}
