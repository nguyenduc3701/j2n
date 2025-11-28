package com.example.j2n.auth_srv.service;

import com.example.j2n.auth_srv.controllers.requests.AssignRoleRequest;
import com.example.j2n.auth_srv.controllers.requests.CreateUserRequest;
import com.example.j2n.auth_srv.controllers.requests.UpdateUserRequest;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public String getUser() {
        return "Register Success";
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
}
