package com.example.j2n.bff_srv.service;

import org.springframework.stereotype.Service;

import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.controller.request.LoginRequest;
import com.example.j2n.bff_srv.controller.request.RegisterRequest;
import com.example.j2n.bff_srv.utils.RestClientUtil;
import org.springframework.http.HttpMethod;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RestClientUtil restClientUtil;
    public Object login(LoginRequest request) {
        return restClientUtil.request(GatewayPath.AUTH_LOGIN, HttpMethod.POST, request, Object.class);
    }
    public Object register(RegisterRequest request) {
        return restClientUtil.request(GatewayPath.AUTH_REGISTER, HttpMethod.POST, request, Object.class);
    }
}
