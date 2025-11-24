package com.example.j2n.bff_srv.auth_srv.service;

import com.example.j2n.bff_srv.auth_srv.controllers.requests.LoginRequest;
import com.example.j2n.bff_srv.auth_srv.controllers.requests.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public String login(LoginRequest request){
        return "Login Success";
    }

    public String register(RegisterRequest request){
        return "Register Success";
    }
}
