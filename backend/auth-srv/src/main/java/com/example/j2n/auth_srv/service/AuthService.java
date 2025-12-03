package com.example.j2n.auth_srv.service;

import com.example.j2n.auth_srv.constant.CommonConst;
import com.example.j2n.auth_srv.constant.MessageEnum;
import com.example.j2n.auth_srv.controllers.requests.ForgotPasswordRequest;
import com.example.j2n.auth_srv.controllers.requests.LoginRequest;
import com.example.j2n.auth_srv.controllers.requests.RegisterRequest;
import com.example.j2n.auth_srv.repository.entity.UserEntity;
import com.example.j2n.auth_srv.service.response.BaseResponse;
import com.example.j2n.auth_srv.service.response.LoginResponse;
import com.example.j2n.auth_srv.service.response.UserItemResponse;
import com.example.j2n.auth_srv.repository.UserRepository;
import com.example.j2n.auth_srv.utils.JwtUtil;
import com.example.j2n.auth_srv.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil;
    private final UserRepository userRepository;

    public BaseResponse<LoginResponse> login(LoginRequest request) {
        log.info("[AUTH-SRV] Login request: {}", request);
        validateLoginRequest(request);
        Optional<UserEntity> user = getUserByUsername(request.getUserName());
        if (!passwordUtil.matches(request.getPassword(), user.get().getPassword())) {
            log.error("Invalid password");
            throw new IllegalArgumentException(MessageEnum.INVALID_CREDENTIALS.getMessage());
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("login_time", System.currentTimeMillis());
        claims.put("user_name", user.get().getUsername());
        claims.put("user_id", user.get().getId().toString());
        claims.put("role_id", user.get().getRoleId().toString());
        String token = jwtUtil.generateToken(claims, user.get().getUsername());
        log.info("[AUTH-SRV] Login Success");
        return BaseResponse.success(new LoginResponse(token));
    }

    public BaseResponse<UserItemResponse> register(RegisterRequest request) {
        log.info("[AUTH-SRV] Register request: {}", request);
        validateUsernameDoesNotExist(request.getUserName());
        UserEntity user = new UserEntity();
        user.setUsername(request.getUserName().trim());
        user.setPassword(passwordUtil.encode(request.getPassword().trim()));
        user.setEmail(request.getEmail().trim());
        user.setFullName(request.getFullName().trim());
        user.setPhoneNumber(request.getPhoneNumber().trim());
        user.setAddress(request.getAddress().trim());
        user.setCompany(request.getCompany().trim());
        user.setRoleId(Objects.requireNonNullElse(request.getRoleId(), CommonConst.ROLE_VISITOR_ID));
        user.setStatus(UserEntity.Status.ACTIVE);
        userRepository.save(user);
        log.info("[AUTH-SRV] Register Success");
        return BaseResponse.success(buildUserItemResponse(user));
    }

    public BaseResponse<String> forgotPassword(ForgotPasswordRequest request) {
        log.info("[AUTH-SRV] Forgot password request: {}", request);
        return BaseResponse.success("Forgot Password Success");
    }

    private Optional<UserEntity> getUserByUsername(String userName) {
        Optional<UserEntity> user = userRepository.findByUsername(userName);
        if (user.isEmpty()) {
            log.error("[AUTH-SRV] User not found: {}", userName);
            throw new IllegalArgumentException(MessageEnum.USER_NOT_FOUND.getMessage());
        }
        return user;
    }

    private void validateLoginRequest(LoginRequest request) {
        if (request.getUserName() == null || request.getUserName().trim().isEmpty()) {
            log.error("[AUTH-SRV] Username is invalid");
            throw new IllegalArgumentException(String.format(MessageEnum.FIELD_REQUIRED.getMessage(), "Username"));
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            log.error("[AUTH-SRV] Password is invalid");
            throw new IllegalArgumentException(String.format(MessageEnum.FIELD_REQUIRED.getMessage(), "Password"));
        }
    }

    public void validateUsernameDoesNotExist(String username) {
        if (userRepository.existsByUsername(username)) {
            log.error("[AUTH-SRV] Username already exists: {}", username);
            throw new IllegalArgumentException(MessageEnum.USERNAME_ALREADY_EXISTS.getMessage());
        }
    }

    public UserItemResponse buildUserItemResponse(UserEntity user) {
        UserItemResponse response = new UserItemResponse();
        response.setId(user.getId().toString());
        response.setUserName(user.getUsername());
        response.setEmail(user.getEmail());
        response.setCreatedAt(user.getCreatedAt().toString());
        return response;
    }
}
