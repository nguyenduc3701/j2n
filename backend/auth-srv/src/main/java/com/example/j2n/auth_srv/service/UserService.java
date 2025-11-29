package com.example.j2n.auth_srv.service;

import com.example.j2n.auth_srv.controllers.requests.AssignRoleRequest;
import com.example.j2n.auth_srv.controllers.requests.CreateUserRequest;
import com.example.j2n.auth_srv.controllers.requests.UpdateUserRequest;
import com.example.j2n.auth_srv.repository.UserRepository;
import com.example.j2n.auth_srv.repository.entity.User;
import com.example.j2n.auth_srv.service.response.BaseResponse;
import com.example.j2n.auth_srv.service.response.UserResponse;
import com.example.j2n.auth_srv.utils.PageUtil;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public BaseResponse<UserResponse> getUsers() {
        PageRequest pageRequest = PageUtil.buildPageRequest(0, 50);
        Page<User> pageData = userRepository.findAllByIsDeletedFalse(pageRequest);
        UserResponse response = new UserResponse();
        response.setUsers(mapUserToUserResponse(pageData.getContent()));
        response.setPage(PageUtil.buildPagingMeta(pageData));
        return BaseResponse.success(response);
    }

    public String getUserById(String userId) {
        return "Register Success" + userId;
    }

    public String getUsersByRoleId(String roleId) {
        return "Register Success" + roleId;
    }

    public String createUser(CreateUserRequest request) {
        return "Register Success";
    }

    public String updateUser(String userId, UpdateUserRequest request) {
        return "Register Success";
    }

    public String deleteUser(String userId) {
        return "Register Success";
    }

    public String assignRoleToUser(String userId, AssignRoleRequest request) {
        return "Register Success";
    }

    private List<UserResponse.UserItem> mapUserToUserResponse(List<User> users) {
        List<UserResponse.UserItem> userResponses = new ArrayList<>();
        for (User user : users) {
            UserResponse.UserItem userResponse = buildUserToUserResponse(user);
            userResponses.add(userResponse);
        }
        return userResponses;
    }

    private UserResponse.UserItem buildUserToUserResponse(User user) {
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

}
