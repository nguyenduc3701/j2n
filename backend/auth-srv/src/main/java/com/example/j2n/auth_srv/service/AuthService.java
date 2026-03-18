package com.example.j2n.auth_srv.service;

import com.example.j2n.auth_srv.controllers.requests.ForgotPasswordRequest;
import com.example.j2n.auth_srv.controllers.requests.LoginRequest;
import com.example.j2n.auth_srv.controllers.requests.RegisterRequest;
import com.example.j2n.auth_srv.exception.FieldExistedException;
import com.example.j2n.auth_srv.exception.InvalidCredentialException;
import com.example.j2n.auth_srv.exception.InvalidRefreshTokenException;
import com.example.j2n.auth_srv.messaging.user.event.UserRegisteredEvent;
import com.example.j2n.auth_srv.messaging.user.publisher.UserEventPublisher;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.j2n.auth_srv.constant.MessageEnum;
import com.example.j2n.aspect.LogAround;

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
    @Value("${application.access-token.expired-time-seconds:14400}")
    private long accessTokenExpireSeconds;

    @Value("${application.refresh-token.expired-time-days:7}")
    private long refreshTokenExpireDays;

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
    private final UserEventPublisher userEventPublisher;

    @LogAround(message = "Login attempt")
    public BaseResponse<LoginResponse> login(LoginRequest request) {
        UserEntity user = findUserByUsername(request.getUserName());
        validateMatchedPassword(request.getPassword(), user.getPassword());
        return ResponseFactory.success(buildLoginResponse(user, generateRandomUUID()));
    }

    @LogAround(message = "Registration attempt")
    public BaseResponse<UserItemResponse> register(RegisterRequest request) {
        validateUsernameAndEmailDoesNotExist(request.getUserName(), request.getEmail());
        UserEntity user = createUserFromRequest(request);
        userRepository.save(user);
        publishUserRegisteredEvent(user);
        return ResponseFactory.success(buildUserItemResponse(user));
    }

    @LogAround(message = "Logout attempt")
    public BaseResponse<String> logout(String accessToken, String refreshToken) {
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
        return ResponseFactory.success("Logout Success");
    }

    @LogAround(message = "Refresh token attempt")
    public BaseResponse<LoginResponse> refreshToken(String token) {
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
        return ResponseFactory.success(buildLoginResponse(user, sessionId));
    }

    @LogAround(message = "Forgot password request")
    public BaseResponse<String> forgotPassword(ForgotPasswordRequest request) {
        // TODO: Implement forgot password logic (send email, generate reset token,
        // etc.)
        return ResponseFactory.success("Forgot Password Success");
    }

    @LogAround(message = "Logout all devices request")
    public BaseResponse<String> logoutAllDevices(String userId) {
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
        return jwtUtil.generate(claims, user.getUsername(), sessionId, accessTokenExpireSeconds);
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
        redisUtil.setValue(sessionKey, sessionValue, accessTokenExpireSeconds, TimeUnit.SECONDS);

        // refresh token value
        String refreshTokenKey = CommonConst.AUTH_REFRESH_PREFIX + refreshToken;
        Map<String, Object> refreshTokenValue = new HashMap<>();
        refreshTokenValue.put(USER_ID_KEY, user.getId().toString());
        refreshTokenValue.put(SESSION_ID_KEY, sessionId);
        redisUtil.setValue(refreshTokenKey, refreshTokenValue, refreshTokenExpireDays, TimeUnit.DAYS);

        // add session id to user sessions set
        String userSessionsKey = CommonConst.AUTH_USER_SESSIONS_PREFIX + user.getId().toString();
        redisUtil.addSet(userSessionsKey, sessionId);
        redisUtil.expire(userSessionsKey, refreshTokenExpireDays, TimeUnit.DAYS);
        log.info("[AUTH-SRV] Inserted record to Redis for user: {}", user.getUsername());
    }

    private UserEntity findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[AUTH-SRV] User not found");
                    return new DataNotFoundException(MessageEnum.USER_NOT_FOUND);
                });
    }

    private String mapRoleIdToText(Long roleId) {
        if (roleId == null) {
            return "VISITOR";
        }
        if (roleId.equals(CommonConst.ROLE_ADMIN_ID)) {
            return "ADMIN";
        }
        if (roleId.equals(CommonConst.ROLE_RECRUITER_ID)) {
            return "RECRUITER";
        }
        if (roleId.equals(CommonConst.ROLE_RENTER_ID)) {
            return "RENTER";
        }
        return "VISITOR";
    }

    private UserRegisteredEvent buildUserRegisteredEvent(UserEntity user) {
        return new UserRegisteredEvent(
                user.getId().toString(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                mapRoleIdToText(user.getRoleId()),
                user.getStatus().name(),
                user.getCreatedAt().toString());
    }

    public void publishUserRegisteredEvent(UserEntity user) {
        if (user.getId() == null) {
            log.error("[AUTH-SRV] User id is null");
            return;
        }
        log.info("[AUTH-SRV] Publishing user registered event for user: {}", user.getUsername());
        UserRegisteredEvent event = buildUserRegisteredEvent(user);
        userEventPublisher.publishUserRegistered(event);
    }
}
