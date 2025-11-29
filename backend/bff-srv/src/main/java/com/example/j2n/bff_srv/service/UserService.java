package com.example.j2n.bff_srv.service;

import org.springframework.stereotype.Service;

import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.utils.RestClientUtil;
import org.springframework.http.HttpMethod;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final RestClientUtil restClientUtil;

    public Object getUsers() {
        return restClientUtil.request(GatewayPath.AUTH_USERS, HttpMethod.GET, null, Object.class);
    }
}
