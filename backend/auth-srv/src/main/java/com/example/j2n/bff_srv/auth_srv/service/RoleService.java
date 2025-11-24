package com.example.j2n.bff_srv.auth_srv.service;

import com.example.j2n.bff_srv.auth_srv.controllers.requests.CreateUserRequest;
import com.example.j2n.bff_srv.auth_srv.controllers.requests.UpdateUserRequest;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    public String getRoles(){
        return "Register Success";
    }
}
