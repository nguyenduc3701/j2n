package com.example.j2n.auth_srv.service;

import com.example.j2n.auth_srv.controllers.requests.ForgotPasswordRequest;
import com.example.j2n.auth_srv.controllers.requests.LoginRequest;
import com.example.j2n.auth_srv.controllers.requests.RegisterRequest;
import com.example.j2n.auth_srv.repository.entity.UserEntity;
import com.example.j2n.auth_srv.service.response.LoginResponse;
import com.example.j2n.auth_srv.service.response.UserItemResponse;
import com.example.j2n.auth_srv.repository.UserRepository;
import com.example.j2n.auth_srv.utils.JwtGeneralUtil;
import com.example.j2n.auth_srv.utils.PasswordUtil;
import com.example.j2n.constants.CommonConst;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.MessageEnum;
import com.example.j2n.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtGeneralUtil jwtUtil;
    private final PasswordUtil passwordUtil;
    private final UserRepository userRepository;
    private final PermissionService permissionService;

    public BaseResponse<LoginResponse> login(LoginRequest request) {
        log.info("[AUTH-SRV] Start Login attempt for user: {}", request.getUserName());
        validateUserNameAndPasswordRequest(request.getUserName(), request.getPassword());
        UserEntity user = findUserByUsername(request.getUserName());
        validateMatchedPassword(request.getPassword(), user.getPassword());
        String token = generateAuthToken(user);
        log.info("[AUTH-SRV] End Login successful for user: {}", user.getUsername());
        return ResponseFactory.success(new LoginResponse(token));
    }

    public BaseResponse<UserItemResponse> register(RegisterRequest request) {
        log.info("[AUTH-SRV] Start Registration attempt for user: {}", request.getUserName());
        validateUserNameAndPasswordRequest(request.getUserName(), request.getPassword());
        validateUsernameAndEmailDoesNotExist(request.getUserName(), request.getEmail());
        UserEntity user = createUserFromRequest(request);
        userRepository.save(user);
        log.info("[AUTH-SRV] End Registration attempt for user: {}", request.getUserName());
        return ResponseFactory.success(buildUserItemResponse(user));
    }

    public BaseResponse<String> forgotPassword(ForgotPasswordRequest request) {
        log.info("[AUTH-SRV] Start Forgot password request for email: {}", request.getEmail());
        // TODO: Implement forgot password logic (send email, generate reset token,
        // etc.)
        log.info("[AUTH-SRV] End Forgot password request for email: {}", request.getEmail());
        return ResponseFactory.success("Forgot Password Success");
    }

    // ==================== Private Helper Methods ====================

    private UserEntity findUserByUsername(String userName) {
        return userRepository.findByUsername(userName)
                .orElseThrow(() -> {
                    log.error("[AUTH-SRV] User not found: {}", userName);
                    return new IllegalArgumentException(MessageEnum.USER_NOT_FOUND.getMessage());
                });
    }

    private void validateMatchedPassword(String rawPassword, String encodedPassword) {
        if (!passwordUtil.matches(rawPassword, encodedPassword)) {
            log.error("[AUTH-SRV] Invalid password attempt");
            throw new IllegalArgumentException(MessageEnum.INVALID_CREDENTIALS.getMessage());
        }
    }

    private String generateAuthToken(UserEntity user) {
        Map<String, Object> claims = buildTokenClaims(user);
        return jwtUtil.generate(claims, user.getUsername());
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

    // ==================== Public Validation Methods ====================

    public UserItemResponse buildUserItemResponse(UserEntity user) {
        UserItemResponse response = new UserItemResponse();
        response.setId(user.getId().toString());
        response.setUserName(user.getUsername());
        response.setEmail(user.getEmail());
        response.setCreatedAt(user.getCreatedAt().toString());
        return response;
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

    public void validateUserNameAndPasswordRequest(String userName, String password) {
        if (userName == null || userName.trim().isEmpty()) {
            log.error("[AUTH-SRV] Username is required");
            throw new IllegalArgumentException(String.format(MessageEnum.FIELD_REQUIRED.getMessage(), "Username"));
        }
        if (password == null || password.trim().isEmpty()) {
            log.error("[AUTH-SRV] Password is required");
            throw new IllegalArgumentException(String.format(MessageEnum.FIELD_REQUIRED.getMessage(), "Password"));
        }
        if (password.length() < CommonConst.PASSWORD_MIN_LENGTH) {
            log.error("[AUTH-SRV] Password is too short");
            throw new IllegalArgumentException(MessageEnum.PASSWORD_TOO_SHORT.getMessage());
        }
    }
}
