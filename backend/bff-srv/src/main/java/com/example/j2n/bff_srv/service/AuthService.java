package com.example.j2n.bff_srv.service;

import com.example.j2n.bff_srv.client.AuthServiceClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.controller.request.LoginRequest;
import com.example.j2n.bff_srv.controller.request.RegisterRequest;
import com.example.j2n.bff_srv.controller.request.CreateUserRequest;
import com.example.j2n.bff_srv.controller.request.SearchUserRequest;
import com.example.j2n.bff_srv.controller.request.UpdateUserRequest;
import com.example.j2n.bff_srv.service.response.ClientLoginResponse;
import com.example.j2n.bff_srv.service.response.LoginResponse;
import com.example.j2n.bff_srv.utils.RestClientUtil;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.exception.UnauthorizedException;
import com.example.j2n.utils.ResponseFactory;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RestClientUtil restClientUtil;

    private final AuthServiceClient authServiceClient;

    public BaseResponse<ClientLoginResponse> login(LoginRequest request, HttpServletResponse response) {
        BaseResponse<LoginResponse> serviceResponse = restClientUtil.request(
                GatewayPath.AUTH_LOGIN_PATH,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<BaseResponse<LoginResponse>>() {
                });
        authServiceClient.updateResponseCredentials(serviceResponse.getData().getAccessToken(),
                serviceResponse.getData().getRefreshToken());
        return ResponseFactory.success(buildClientLoginResponse(serviceResponse.getData().getAccessToken()));
    }

    public Object register(RegisterRequest request) {
        return restClientUtil.request(GatewayPath.AUTH_REGISTER_PATH, HttpMethod.POST, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object getListUsers(SearchUserRequest request) {
        return restClientUtil.request(GatewayPath.AUTH_GET_LIST_USERS_PATH, HttpMethod.POST, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object getUserById(String id) {
        return restClientUtil.request(String.format(GatewayPath.AUTH_USER_ID_PATH, id), HttpMethod.GET, null,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object getUserMe() {
        return restClientUtil.request(GatewayPath.AUTH_ME_PATH, HttpMethod.GET, null,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object createUser(CreateUserRequest request) {
        return restClientUtil.request(GatewayPath.AUTH_CREATE_USER_PATH, HttpMethod.POST, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object updateUser(String id, UpdateUserRequest request) {
        return restClientUtil.request(String.format(GatewayPath.AUTH_UPDATE_USER_PATH, id), HttpMethod.PUT, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object deleteUser(String id) {
        return restClientUtil.request(String.format(GatewayPath.AUTH_DELETE_USER_PATH, id), HttpMethod.DELETE, null,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object getRoles() {
        return restClientUtil.request(GatewayPath.AUTH_GET_ROLES_PATH, HttpMethod.GET, null,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object getPermissions() {
        return restClientUtil.request(GatewayPath.AUTH_GET_PERMISSIONS_PATH, HttpMethod.GET, null,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object getPermissionsByRoleId(String roleId) {
        return restClientUtil.request(String.format(GatewayPath.AUTH_GET_PERMISSIONS_BY_ROLE_ID_PATH, roleId),
                HttpMethod.GET, null, new ParameterizedTypeReference<Object>() {
                });
    }

    public Object logout() {
        return restClientUtil.request(GatewayPath.AUTH_LOGOUT_PATH, HttpMethod.POST, null,
                new ParameterizedTypeReference<Object>() {
                });
    }

    private ClientLoginResponse buildClientLoginResponse(String accessToken) {
        if (accessToken == null) {
            throw new UnauthorizedException(BaseMessageEnum.UNAUTHORIZED);
        }
        ClientLoginResponse response = new ClientLoginResponse();
        response.setAccessToken(accessToken);
        return response;
    }
}
