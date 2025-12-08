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
import com.example.j2n.auth_srv.service.response.UserResponse;
import com.example.j2n.auth_srv.repository.UserRepository;
import com.example.j2n.auth_srv.utils.JwtUtil;
import com.example.j2n.auth_srv.utils.PasswordUtil;
import com.example.j2n.auth_srv.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.j2n.auth_srv.controllers.requests.VerifyRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PermissionService permissionService;

    public BaseResponse<LoginResponse> login(LoginRequest request) {
        log.info("[AUTH-SRV] Login attempt for user: {}", request.getUserName());
        validateLoginRequest(request);
        UserEntity user = findUserByUsername(request.getUserName());
        validatePassword(request.getPassword(), user.getPassword());
        String token = generateAuthToken(user);
        log.info("[AUTH-SRV] Login successful for user: {}", user.getUsername());
        return ResponseFactory.success(new LoginResponse(token));
    }

    public BaseResponse<UserItemResponse> register(RegisterRequest request) {
        log.info("[AUTH-SRV] Registration attempt for user: {}", request.getUserName());
        validateUsernameAndEmailDoesNotExist(request.getUserName(), request.getEmail());
        validatePassword(request.getPassword());
        UserEntity user = createUserFromRequest(request);
        user = userRepository.save(user);
        log.info("[AUTH-SRV] User registered successfully: {}", user.getUsername());
        return ResponseFactory.success(buildUserItemResponse(user));
    }

    public BaseResponse<String> forgotPassword(ForgotPasswordRequest request) {
        log.info("[AUTH-SRV] Forgot password request for email: {}", request.getEmail());
        // TODO: Implement forgot password logic (send email, generate reset token,
        // etc.)
        return ResponseFactory.success("Forgot Password Success");
    }

    public BaseResponse<UserResponse.UserItem> verify(VerifyRequest request) {
        log.info("[AUTH-SRV] Verify request for user: {}", request.getUserId());
        validateUser(request);
        UserResponse.UserItem userItem = userService.getUserItemById(request.getUserId());
        List<String> permissions = permissionService.getPermissionsByRoleId(request.getRoleId()).getData();
        userItem.setPermissions(permissions);
        log.info("[AUTH-SRV] Verify request for user: {} success", request.getUserId());
        return ResponseFactory.success(userItem);
    }

    // ==================== Private Helper Methods ====================

    private UserEntity findUserByUsername(String userName) {
        return userRepository.findByUsername(userName)
                .orElseThrow(() -> {
                    log.error("[AUTH-SRV] User not found: {}", userName);
                    return new IllegalArgumentException(MessageEnum.USER_NOT_FOUND.getMessage());
                });
    }

    public void validateUser(VerifyRequest request) {
        UserEntity user = userRepository.findById(Long.parseLong(request.getUserId()))
                .orElseThrow(() -> {
                    log.error("[AUTH-SRV] User not found: {}", request.getUserId());
                    return new IllegalArgumentException(MessageEnum.INVALID_USER_INFORMATION.getMessage());
                });

        if (!user.getRoleId().toString().equals(request.getRoleId())) {
            log.error("[AUTH-SRV] Role mismatch. Expected: {}, Got: {}", user.getRoleId(), request.getRoleId());
            throw new IllegalArgumentException(MessageEnum.INVALID_USER_INFORMATION.getMessage());
        }

        List<String> actualPermissions = permissionService.getPermissionsByRoleId(user.getRoleId().toString())
                .getData();
        if (request.getPermissions() != null && !actualPermissions.equals(request.getPermissions())) {
            log.error("[AUTH-SRV] Permissions mismatch for user: {}", request.getUserId());
            throw new IllegalArgumentException(MessageEnum.INVALID_USER_INFORMATION.getMessage());
        }
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordUtil.matches(rawPassword, encodedPassword)) {
            log.error("[AUTH-SRV] Invalid password attempt");
            throw new IllegalArgumentException(MessageEnum.INVALID_CREDENTIALS.getMessage());
        }
    }

    private String generateAuthToken(UserEntity user) {
        Map<String, Object> claims = buildTokenClaims(user);
        return jwtUtil.generateToken(claims, user.getUsername());
    }

    private Map<String, Object> buildTokenClaims(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();
        BaseResponse<List<String>> permissions = permissionService.getPermissionsByRoleId(user.getRoleId().toString());
        claims.put("login_time", System.currentTimeMillis());
        claims.put("user_id", user.getId().toString());
        claims.put("user_name", user.getUsername());
        claims.put("role_id", user.getRoleId().toString());
        claims.put("permissions", permissions.getData());
        return claims;
    }

    private UserEntity createUserFromRequest(RegisterRequest request) {
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
        return user;
    }

    // ==================== Validation Methods ====================

    private void validateLoginRequest(LoginRequest request) {
        if (request.getUserName() == null || request.getUserName().trim().isEmpty()) {
            log.error("[AUTH-SRV] Username is required");
            throw new IllegalArgumentException(String.format(MessageEnum.FIELD_REQUIRED.getMessage(), "Username"));
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            log.error("[AUTH-SRV] Password is required");
            throw new IllegalArgumentException(String.format(MessageEnum.FIELD_REQUIRED.getMessage(), "Password"));
        }
    }

    private void validatePassword(String password) {
        if (password.length() < CommonConst.PASSWORD_MIN_LENGTH) {
            log.error("[AUTH-SRV] Password is too short");
            throw new IllegalArgumentException(MessageEnum.PASSWORD_TOO_SHORT.getMessage());
        }
    }

    public void validateUsernameAndEmailDoesNotExist(String username, String email) {
        if (userRepository.existsByEmail(email)) {
            log.error("[AUTH-SRV] Email already exists: {}", email);
            throw new IllegalArgumentException(MessageEnum.EMAIL_ALREADY_EXISTS.getMessage());
        }
        if (userRepository.existsByUsername(username)) {
            log.error("[AUTH-SRV] Username already exists: {}", username);
            throw new IllegalArgumentException(MessageEnum.USERNAME_ALREADY_EXISTS.getMessage());
        }
    }

    // ==================== Public Utility Methods ====================

    public UserItemResponse buildUserItemResponse(UserEntity user) {
        UserItemResponse response = new UserItemResponse();
        response.setId(user.getId().toString());
        response.setUserName(user.getUsername());
        response.setEmail(user.getEmail());
        response.setCreatedAt(user.getCreatedAt().toString());
        return response;
    }
}
