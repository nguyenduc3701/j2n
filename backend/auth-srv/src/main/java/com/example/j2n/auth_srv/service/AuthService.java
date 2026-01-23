package com.example.j2n.auth_srv.service;

import com.example.j2n.auth_srv.controllers.requests.ForgotPasswordRequest;
import com.example.j2n.auth_srv.controllers.requests.LoginRequest;
import com.example.j2n.auth_srv.controllers.requests.LogoutRequest;
import com.example.j2n.auth_srv.controllers.requests.RefreshTokenRequest;
import com.example.j2n.auth_srv.controllers.requests.RegisterRequest;
import com.example.j2n.auth_srv.exception.FieldExistedException;
import com.example.j2n.auth_srv.exception.InvalidCredentialException;
import com.example.j2n.auth_srv.exception.InvalidRefreshTokenException;
import com.example.j2n.auth_srv.repository.entity.UserEntity;
import com.example.j2n.auth_srv.service.response.LoginResponse;
import com.example.j2n.auth_srv.service.response.UserItemResponse;
import com.example.j2n.auth_srv.repository.UserRepository;
import com.example.j2n.auth_srv.utils.JwtGeneralUtil;
import com.example.j2n.auth_srv.utils.PasswordUtil;
import com.example.j2n.constants.CommonConst;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.exception.UnauthorizedException;
import com.example.j2n.utils.ResponseFactory;
import com.example.j2n.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.j2n.auth_srv.constant.MessageEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private static final long ACCESS_TOKEN_EXPIRE_SECONDS = 300; //14400;
    private static final long REFRESH_TOKEN_EXPIRE_DAYS = 7;

    public static final String USER_ID_KEY = "user_id";
    public static final String USER_NAME_KEY = "user_name";
    public static final String SESSION_ID_KEY = "session_id";
    public static final String REFRESH_TOKEN_KEY = "refresh_token";
    public static final String ROLE_ID_KEY = "role_id";
    public static final String PERMISSIONS_KEY = "permissions";
    public static final String LOGIN_TIME_KEY = "login_time";

    private final JwtGeneralUtil jwtUtil;
    private final PasswordUtil passwordUtil;
    private final UserRepository userRepository;
    private final PermissionService permissionService;
    private final RedisUtil redisUtil;

    public BaseResponse<LoginResponse> login(LoginRequest request) {
        log.info("[AUTH-SRV] Start Login attempt for user: {}", request.getUserName());
        validateUserNameAndPasswordRequest(request.getUserName(), request.getPassword());
        UserEntity user = findUserByUsername(request.getUserName());
        validateMatchedPassword(request.getPassword(), user.getPassword());
        log.info("[AUTH-SRV] End Login successful for user: {}", user.getUsername());
        return ResponseFactory.success(buildLoginResponse(user, generateRandomUUID()));
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

    public BaseResponse<String> logout(String accessToken, String refreshToken) {
        log.info("[AUTH-SRV] Start Logout attempt");
        validateLogoutRequest(accessToken,refreshToken);
        String token = accessToken.substring(7);
        Claims claims = jwtUtil.validate(token);
        String sessionId = claims.get(SESSION_ID_KEY, String.class);
        String userId = claims.get(USER_ID_KEY, String.class);
        if (userId == null || sessionId == null) {
            throw new UnauthorizedException(MessageEnum.TOKEN_INVALID);
        }
        String sessionKey = CommonConst.AUTH_SESSION_PREFIX + sessionId;
        redisUtil.deleteKey(sessionKey);
        String refreshKey = CommonConst.AUTH_REFRESH_PREFIX + refreshToken;
        redisUtil.deleteKey(refreshKey);
        String userSessionsKey = CommonConst.AUTH_USER_SESSIONS_PREFIX + userId;
        redisUtil.removeSet(userSessionsKey, sessionId);
        log.info("[AUTH-SRV] End Logout success for userId={}", userId);
        return ResponseFactory.success("Logout Success");
    }

    public BaseResponse<LoginResponse> refreshToken(String token) {
        log.info("[AUTH-SRV] Start Refresh token attempt");
        validateRefreshTokenRequest(token);
        String refreshKey = CommonConst.AUTH_REFRESH_PREFIX + token;
        if (!redisUtil.hasKey(refreshKey)) {
            throw new InvalidRefreshTokenException();
        }
        Map<String, Object> refreshValue = (Map<String, Object>) redisUtil.getValue(refreshKey);
        if (refreshValue == null) {
            throw new InvalidRefreshTokenException();
        }
        String userId = (String) refreshValue.get(USER_ID_KEY);
        String sessionId = (String) refreshValue.get(SESSION_ID_KEY);
        redisUtil.deleteKey(refreshKey);
        UserEntity user = findUserById(Long.valueOf(userId));
        log.info("[AUTH-SRV] End Refresh token success userId={}", userId);
        return ResponseFactory.success(buildLoginResponse(user, sessionId));
    }

    public BaseResponse<String> forgotPassword(ForgotPasswordRequest request) {
        log.info("[AUTH-SRV] Start Forgot password request for email: {}", request.getEmail());
        // TODO: Implement forgot password logic (send email, generate reset token,
        // etc.)
        log.info("[AUTH-SRV] End Forgot password request for email: {}", request.getEmail());
        return ResponseFactory.success("Forgot Password Success");
    }

    public BaseResponse<String> logoutAllDevices(String userId) {
        log.info("[AUTH-SRV] Start Logout all devices request for user: {}", userId);
        String userSessionsKey = CommonConst.AUTH_USER_SESSIONS_PREFIX + userId;
        Set<Object> sessions = redisUtil.getSet(userSessionsKey);
        if (sessions != null && !sessions.isEmpty()) {
            List<String> keysToDelete = new ArrayList<>();
            for (Object sessionId : sessions) {
                String sId = (String) sessionId;
                keysToDelete.add(CommonConst.AUTH_SESSION_PREFIX + sId);
            }
            redisUtil.deleteKeys(keysToDelete);
        }
        redisUtil.deleteKey(userSessionsKey);
        log.info("[AUTH-SRV] End Logout all devices request for user: {}", userId);
        return ResponseFactory.success("Logout All Devices Success");
    }

    // ==================== Private Helper Methods ====================

    private UserEntity findUserByUsername(String userName) {
        return userRepository.findByUsername(userName)
                .orElseThrow(() -> {
                    log.error("[AUTH-SRV] User not found: {}", userName);
                    return new DataNotFoundException(MessageEnum.USER_NOT_FOUND);
                });
    }

    private void validateMatchedPassword(String rawPassword, String encodedPassword) {
        if (!passwordUtil.matches(rawPassword, encodedPassword)) {
            log.error("[AUTH-SRV] Invalid password attempt");
            throw new InvalidCredentialException();
        }
    }

    private String generateAuthToken(UserEntity user, String sessionId) {
        Map<String, Object> claims = buildTokenClaims(user, sessionId);
        return jwtUtil.generate(claims, user.getUsername(), sessionId, ACCESS_TOKEN_EXPIRE_SECONDS);
    }

    private LoginResponse buildLoginResponse(UserEntity user, String sessionId) {
        String token = generateAuthToken(user, sessionId);
        String refreshToken = generateRandomUUID();
        insertToRedis(user, sessionId, refreshToken);
        return new LoginResponse(token, refreshToken);
    }

    private Map<String, Object> buildTokenClaims(UserEntity user, String sessionId) {
        Map<String, Object> claims = new HashMap<>();
        BaseResponse<List<String>> permissions = permissionService.getPermissionsByRoleId(user.getRoleId().toString());
        claims.put(LOGIN_TIME_KEY, System.currentTimeMillis());
        claims.put(USER_ID_KEY, user.getId().toString());
        claims.put(USER_NAME_KEY, user.getUsername());
        claims.put(ROLE_ID_KEY, user.getRoleId().toString());
        claims.put(PERMISSIONS_KEY, permissions.getData());
        claims.put(SESSION_ID_KEY, sessionId);
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
            throw new FieldExistedException("Email");
        }
        if (userRepository.existsByUsername(username)) {
            log.error("[AUTH-SRV] Username already exists: {}", username);
            throw new FieldExistedException("Username");
        }
    }

    public void validateUserNameAndPasswordRequest(String userName, String password) {
        if (userName == null || userName.trim().isEmpty()) {
            log.error("[AUTH-SRV] Username is required");
            throw new InvalidInputException(BaseMessageEnum.FIELD_REQUIRED.withArgs("Username"));
        }
        if (password == null || password.trim().isEmpty()) {
            log.error("[AUTH-SRV] Password is required");
            throw new InvalidInputException(BaseMessageEnum.FIELD_REQUIRED.withArgs("Password"));
        }
        if (password.length() < CommonConst.PASSWORD_MIN_LENGTH) {
            log.error("[AUTH-SRV] Password is too short");
            throw new InvalidInputException(MessageEnum.PASSWORD_TOO_SHORT);
        }
    }

    private String generateRandomUUID() {
        return UUID.randomUUID().toString();
    }

    private void insertToRedis(UserEntity user, String sessionId, String refreshToken) {
        log.info("[AUTH-SRV] Inserting record to Redis for user: {}", user.getUsername());
        // session value
        String sessionKey = CommonConst.AUTH_SESSION_PREFIX + sessionId;
        Map<String, Object> sessionValue = new HashMap<>();
        sessionValue.put(USER_ID_KEY, user.getId().toString());
        sessionValue.put(USER_NAME_KEY, user.getUsername());
        sessionValue.put(ROLE_ID_KEY, user.getRoleId().toString());
        redisUtil.setValue(sessionKey, sessionValue, ACCESS_TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);

        // refresh token value
        String refreshTokenKey = CommonConst.AUTH_REFRESH_PREFIX + refreshToken;
        Map<String, Object> refreshTokenValue = new HashMap<>();
        refreshTokenValue.put(USER_ID_KEY, user.getId().toString());
        refreshTokenValue.put(SESSION_ID_KEY, sessionId);
        redisUtil.setValue(refreshTokenKey, refreshTokenValue, REFRESH_TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);

        // add session id to user sessions set
        String userSessionsKey = CommonConst.AUTH_USER_SESSIONS_PREFIX + user.getId().toString();
        redisUtil.addSet(userSessionsKey, sessionId);
        redisUtil.expire(userSessionsKey, REFRESH_TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        log.info("[AUTH-SRV] Inserted record to Redis for user: {}", user.getUsername());
    }

    private void validateLogoutRequest(String accessToken, String refreshToken) {
        if (accessToken == null || refreshToken == null) {
            log.error("[AUTH-SRV] Token is required");
            throw new InvalidInputException(BaseMessageEnum.FIELD_REQUIRED.withArgs("Token"));
        }
    }

    private void validateRefreshTokenRequest(String token) {
        if (token == null) {
            log.error("[AUTH-SRV] Refresh token is required");
            throw new InvalidInputException(BaseMessageEnum.FIELD_REQUIRED.withArgs("Token"));
        }
    }

    private UserEntity findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[AUTH-SRV] User not found");
                    return new DataNotFoundException(MessageEnum.USER_NOT_FOUND);
                });
    }
}
