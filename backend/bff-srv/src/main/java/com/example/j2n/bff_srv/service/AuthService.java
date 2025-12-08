package com.example.j2n.bff_srv.service;

import org.springframework.stereotype.Service;

import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.controller.request.LoginRequest;
import com.example.j2n.bff_srv.controller.request.RegisterRequest;
import com.example.j2n.bff_srv.controller.request.CreateUserRequest;
import com.example.j2n.bff_srv.utils.RestClientUtil;
import org.springframework.http.HttpMethod;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RestClientUtil restClientUtil;

    public Object login(LoginRequest request) {
        return restClientUtil.request(GatewayPath.AUTH_LOGIN_PATH, HttpMethod.POST, request, Object.class);
    }

    public Object register(RegisterRequest request) {
        return restClientUtil.request(GatewayPath.AUTH_REGISTER_PATH, HttpMethod.POST, request, Object.class);
    }

    public Object getListUsers() {
        return restClientUtil.request(GatewayPath.AUTH_GET_LIST_USERS_PATH, HttpMethod.GET, null, Object.class);
    }

    public Object getUserById(String id) {
        return restClientUtil.request(String.format(GatewayPath.AUTH_USER_ID_PATH, id), HttpMethod.GET, null,
                Object.class);
    }

    public Object getUserMe() {
        return restClientUtil.request(GatewayPath.AUTH_ME_PATH, HttpMethod.GET, null, Object.class);
    }

    public Object createUser(CreateUserRequest request) {
        return restClientUtil.request(GatewayPath.AUTH_CREATE_USER_PATH, HttpMethod.POST, request, Object.class);
    }
}
