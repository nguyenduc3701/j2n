package com.example.j2n.bff_srv.auth_srv.service;

import com.example.j2n.bff_srv.auth_srv.constant.MessageEnum;
import com.example.j2n.bff_srv.auth_srv.controllers.requests.ForgotPasswordRequest;
import com.example.j2n.bff_srv.auth_srv.controllers.requests.LoginRequest;
import com.example.j2n.bff_srv.auth_srv.controllers.requests.RegisterRequest;
import com.example.j2n.bff_srv.auth_srv.repository.entity.User;
import com.example.j2n.bff_srv.auth_srv.repository.UserRepository;
import com.example.j2n.bff_srv.auth_srv.utils.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    
    public String login(LoginRequest request) {
        return "Login Success";
    }

    public String register(RegisterRequest request) {
        validateUsernameDoesNotExist(request.getUserName());
        User user = new User();
        user.setUsername(request.getUserName());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setCompany(request.getCompany());
        user.setRoleId(request.getRoleId());
        userRepository.save(user);
        return "Register Success";
    }

    public String forgotPassword(ForgotPasswordRequest request) {
        return "Forgot Password Success";
    }

    private void validateUsernameDoesNotExist(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(MessageEnum.USERNAME_ALREADY_EXISTS.getMessage());
        }
    }
}
