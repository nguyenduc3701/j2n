package com.example.j2n.auth_srv.service;

import com.example.j2n.auth_srv.constant.CommonConst;
import com.example.j2n.auth_srv.constant.MessageEnum;
import com.example.j2n.auth_srv.controllers.requests.CreateUserRequest;
import com.example.j2n.auth_srv.controllers.requests.UpdateUserRequest;
import com.example.j2n.auth_srv.repository.UserRepository;
import com.example.j2n.auth_srv.repository.entity.UserEntity;
import com.example.j2n.auth_srv.service.response.BaseResponse;
import com.example.j2n.auth_srv.service.response.UserItemResponse;
import com.example.j2n.auth_srv.service.response.UserResponse;
import com.example.j2n.auth_srv.utils.PageUtil;
import com.example.j2n.auth_srv.utils.PasswordUtil;
import com.example.j2n.auth_srv.utils.ResponseFactory;
import com.example.j2n.auth_srv.utils.common.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PermissionService permissionService;
    private final AuthService authService;
    private final PasswordUtil passwordUtil;
    private final CurrentUser currentUser;

    public BaseResponse<UserResponse> getUsers() {
        log.info("[AUTH-SRV] Fetching user list");
        PageRequest pageRequest = PageUtil.buildPageRequest(0, 50);
        Page<UserEntity> pageData = userRepository.findAllByIsDeletedFalse(pageRequest);
        UserResponse response = new UserResponse();
        response.setUsers(mapUserEntitiesToUserItems(pageData.getContent()));
        response.setPage(PageUtil.buildPagingMeta(pageData));
        log.info("[AUTH-SRV] Retrieved {} users", pageData.getTotalElements());
        return ResponseFactory.success(response);
    }

    public BaseResponse<UserResponse.UserItem> getMe() {
        log.info("[AUTH-SRV] Fetching current user details");
        UserResponse.UserItem userItem = getUserItemById(currentUser.getId());
        List<String> permissions = permissionService
                .getPermissionsByRoleId(userItem.getRoleId())
                .getData();
        userItem.setPermissions(permissions);
        log.info("[AUTH-SRV] Current user retrieved: {}", userItem.getUserName());
        return ResponseFactory.success(userItem);
    }

    public BaseResponse<UserResponse.UserItem> getUserById(String userId) {
        log.info("[AUTH-SRV] Fetching user by ID: {}", userId);
        UserResponse.UserItem user = getUserItemById(userId);
        log.info("[AUTH-SRV] User retrieved: {}", user.getUserName());
        return ResponseFactory.success(user);
    }

    public BaseResponse<UserItemResponse> createUser(CreateUserRequest request) {
        log.info("[AUTH-SRV] Creating user: {}", request.getUserName());
        validateCanCreateUserByRoleId();
        authService.validateUsernameAndEmailDoesNotExist(request.getUserName(), request.getEmail());
        validateAllowRole(request.getRoleId());
        UserEntity user = buildUserFromCreateRequest(request);
        user = userRepository.save(user);
        log.info("[AUTH-SRV] User created successfully: {}", user.getUsername());
        return ResponseFactory.success(authService.buildUserItemResponse(user));
    }

    public BaseResponse<UserItemResponse> updateUser(String userId, UpdateUserRequest request) {
        log.info("[AUTH-SRV] Updating user ID: {}", userId);
        UserEntity user = findUserByIdOrThrow(userId);
        validateAllowRole(request.getRoleId());
        applyUpdateFields(user, request);
        user = userRepository.save(user);
        log.info("[AUTH-SRV] User updated successfully: {}", user.getUsername());
        return ResponseFactory.success(authService.buildUserItemResponse(user));
    }

    public BaseResponse<Object> deleteUser(String userId) {
        log.info("[AUTH-SRV] Deleting user ID: {}", userId);
        validateCanCreateUserByRoleId();
        UserEntity user = findUserByIdOrThrow(userId);
        user.setStatus(UserEntity.Status.INACTIVE);
        user.setIsDeleted(true);
        userRepository.save(user);
        log.info("[AUTH-SRV] User deleted successfully: {}", user.getUsername());
        return ResponseFactory.of(MessageEnum.DELETE_USER_SUCCESS, null);
    }

    public UserEntity findUserByIdOrThrow(String userId) {
        return userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> {
                    log.error("[AUTH-SRV] User not found: {}", userId);
                    return new IllegalArgumentException(MessageEnum.USER_NOT_FOUND.getMessage());
                });
    }

    public UserResponse.UserItem getUserItemById(String userId) {
        UserEntity user = findUserByIdOrThrow(userId);
        return buildUserItemFromEntity(user);
    }

    private UserEntity buildUserFromCreateRequest(CreateUserRequest request) {
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
        return user;
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
        if (request.getRoleId() != null) {
            user.setRoleId(request.getRoleId());
        }
    }

    // ==================== Mapping Methods ====================

    public List<UserResponse.UserItem> mapUserEntitiesToUserItems(List<UserEntity> users) {
        return users.stream()
                .map(this::buildUserItemFromEntity)
                .toList();
    }

    public UserResponse.UserItem buildUserItemFromEntity(UserEntity user) {
        UserResponse.UserItem userItem = new UserResponse.UserItem();
        userItem.setId(user.getId());
        userItem.setUserName(user.getUsername());
        userItem.setFullName(user.getFullName());
        userItem.setEmail(user.getEmail());
        userItem.setPhoneNumber(user.getPhoneNumber());
        userItem.setBirth(user.getBirth());
        userItem.setImageUrl(user.getImageUrl());
        userItem.setRoomId(Objects.toString(user.getRoomId(), null));
        userItem.setAddress(user.getAddress());
        userItem.setCompany(user.getCompany());
        userItem.setRoleId(mapRoleIdToText(user.getRoleId()));
        userItem.setStatus(user.getStatus().name());
        userItem.setCreatedAt(user.getCreatedAt().toString());
        userItem.setUpdatedAt(user.getUpdatedAt().toString());
        return userItem;
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

    // ==================== Validation Methods ====================

    private void validateCanCreateUserByRoleId() {
        String currentRoleId = currentUser.getRoleId();
        if (!currentRoleId.equals(CommonConst.ROLE_ADMIN_ID.toString())
                && !currentRoleId.equals(CommonConst.ROLE_RECRUITER_ID.toString())) {
            log.error("[AUTH-SRV] User lacks permission to create/delete users. Role ID: {}", currentRoleId);
            throw new IllegalArgumentException(MessageEnum.ROLE_NOT_ALLOW_ACTION.getMessage());
        }
    }

    private void validateAllowRole(Long roleId) {
        if (CommonConst.ROLE_ADMIN_ID.equals(roleId)) {
            log.error("[AUTH-SRV] Cannot create/update user with ADMIN role. Role ID: {}", roleId);
            throw new IllegalArgumentException(MessageEnum.ROLE_NOT_ALLOW_CREATE_USER.getMessage());
        }
    }
}
