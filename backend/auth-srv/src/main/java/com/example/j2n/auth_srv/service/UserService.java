package com.example.j2n.auth_srv.service;

import com.example.j2n.auth_srv.constant.CommonConst;
import com.example.j2n.auth_srv.constant.MessageEnum;
import com.example.j2n.auth_srv.controllers.requests.AssignRoleRequest;
import com.example.j2n.auth_srv.controllers.requests.CreateUserRequest;
import com.example.j2n.auth_srv.controllers.requests.UpdateUserRequest;
import com.example.j2n.auth_srv.repository.UserRepository;
import com.example.j2n.auth_srv.repository.entity.UserEntity;
import com.example.j2n.auth_srv.service.response.BaseResponse;
import com.example.j2n.auth_srv.service.response.UserItemResponse;
import com.example.j2n.auth_srv.service.response.UserResponse;
import com.example.j2n.auth_srv.utils.PageUtil;
import com.example.j2n.auth_srv.utils.PasswordUtil;
import com.example.j2n.auth_srv.utils.common.CurrentUser;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final CurrentUser currentUser;
    private final PermissionService permissionService;
    private final AuthService authService;
    private final PasswordUtil passwordUtil;

    public BaseResponse<UserResponse> getUsers() {
        log.info("[AUTH-SRV] Get users");
        PageRequest pageRequest = PageUtil.buildPageRequest(0, 50);
        Page<UserEntity> pageData = userRepository.findAllByIsDeletedFalse(pageRequest);
        UserResponse response = new UserResponse();
        response.setUsers(mapUserToUserResponse(pageData.getContent()));
        response.setPage(PageUtil.buildPagingMeta(pageData));
        log.info("[AUTH-SRV] Get users success");
        return BaseResponse.success(response);
    }

    public BaseResponse<UserResponse.UserItem> getMe() {
        log.info("[AUTH-SRV] Get current user details");
        UserResponse.UserItem userItem = getUserItemById(currentUser.getCurrentUserId());
        BaseResponse<List<String>> permissions = permissionService
                .getPermissionsByRoleId(currentUser.getCurrentRoleId());
        userItem.setPermissions(permissions.getData());
        log.info("[AUTH-SRV] Get current user details success for user");
        return BaseResponse.success(userItem);
    }

    public BaseResponse<UserResponse.UserItem> getUserById(String userId) {
        UserResponse.UserItem user = getUserItemById(userId);
        return BaseResponse.success(user);
    }

    public BaseResponse<UserItemResponse> createUser(CreateUserRequest request) {
        log.info("[AUTH-SRV] Create user");
        authService.validateUsernameDoesNotExist(request.getUserName());
        validateAllowRole(request.getRoleId());
        UserEntity user = new UserEntity();
        user.setUsername(request.getUserName().trim());
        user.setPassword(passwordUtil.encode(request.getPassword().trim()));
        user.setEmail(request.getEmail().trim());
        user.setFullName(request.getFullName().trim());
        user.setPhoneNumber(request.getPhoneNumber().trim());
        user.setAddress(request.getAddress().trim());
        user.setCompany(request.getCompany().trim());
        user.setRoleId(Objects.requireNonNullElse(request.getRoleId(), CommonConst.ROLE_VISITOR_ID));
        user.setStatus(UserEntity.Status.INACTIVE);
        user.setRoomId(request.getRoomId());
        user.setBirth(request.getBirth());
        userRepository.save(user);
        log.info("[AUTH-SRV] Create user success");
        return BaseResponse.success(authService.buildUserItemResponse(user));
    }

    public BaseResponse<UserItemResponse> updateUser(String userId, UpdateUserRequest request) {
        UserEntity user = validateUserById(userId);
        validateAllowRole(request.getRoleId());
        applyUpdateFields(user, request);
        userRepository.save(user);
        return BaseResponse.success(authService.buildUserItemResponse(user));
    }

    public BaseResponse<Object> deleteUser(String userId) {
        validateUserById(userId);
        userRepository.deleteById(Long.parseLong(userId));
        return BaseResponse.success(Map.of("id", userId));
    }

    private void validateAllowRole(Long roleId) {
        if (roleId == CommonConst.ROLE_ADMIN_ID) {
            throw new IllegalArgumentException(MessageEnum.ROLE_NOT_ALLOW.getMessage());
        }
    }

    private UserResponse.UserItem getUserItemById(String userId) {
        Optional<UserEntity> user = userRepository.findById(Long.parseLong(userId));
        if (user.isEmpty()) {
            log.error("[AUTH-SRV] User not found: {}", userId);
            throw new IllegalArgumentException(MessageEnum.USER_NOT_FOUND.getMessage());
        }
        return buildUserToUserResponse(user.get());
    }

    private UserEntity validateUserById(String userId) {
        Optional<UserEntity> user = userRepository.findById(Long.parseLong(userId));
        if (user.isEmpty()) {
            log.error("[AUTH-SRV] User not found: {}", userId);
            throw new IllegalArgumentException(MessageEnum.USER_NOT_FOUND.getMessage());
        }
        return user.get();
    }

    private List<UserResponse.UserItem> mapUserToUserResponse(List<UserEntity> users) {
        List<UserResponse.UserItem> userResponses = new ArrayList<>();
        for (UserEntity user : users) {
            UserResponse.UserItem userResponse = buildUserToUserResponse(user);
            userResponses.add(userResponse);
        }
        return userResponses;
    }

    private UserResponse.UserItem buildUserToUserResponse(UserEntity user) {
        UserResponse.UserItem userResponse = new UserResponse.UserItem();
        userResponse.setId(user.getId());
        userResponse.setUserName(user.getUsername());
        userResponse.setFullName(user.getFullName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setBirth(user.getBirth());
        userResponse.setImageUrl(user.getImageUrl());
        userResponse.setRoomId(Objects.toString(user.getRoomId(), null));
        userResponse.setAddress(user.getAddress());
        userResponse.setCompany(user.getCompany());
        userResponse.setRoleId(mapRoleIdToText(user.getRoleId()));
        userResponse.setStatus(user.getStatus().name());
        userResponse.setCreatedAt(user.getCreatedAt().toString());
        userResponse.setUpdatedAt(user.getUpdatedAt().toString());
        return userResponse;
    }

    private String mapRoleIdToText(Long roleId) {
        if (roleId == null)
            return "VISITOR";
        switch (roleId.intValue()) {
            case 1:
                return "ADMIN";
            case 2:
                return "RECRUITER";
            case 3:
                return "RENTER";
            default:
                return "VISITOR";
        }
    }

    private void applyUpdateFields(UserEntity user, UpdateUserRequest request) {
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getCompany() != null) {
            user.setCompany(request.getCompany());
        }
        if (request.getBirth() != null) {
            user.setBirth(request.getBirth());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        // if (request.getRoleId() != null) {
        // var role = roleRepository.findById(request.getRoleId())
        // .orElseThrow(() -> new RuntimeException("Role not found"));
        // user.setRole(role);
        // }

    }

}
