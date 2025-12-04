package com.example.j2n.auth_srv.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.j2n.auth_srv.controllers.requests.CreateUserRequest;
import com.example.j2n.auth_srv.controllers.requests.UpdateUserRequest;
import com.example.j2n.auth_srv.service.UserService;
import com.example.j2n.auth_srv.service.response.BaseResponse;
import com.example.j2n.auth_srv.service.response.UserItemResponse;
import com.example.j2n.auth_srv.service.response.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

        private MockMvc mockMvc;

        @Mock
        private UserService userService;

        @InjectMocks
        private UserController userController;

        private ObjectMapper objectMapper;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
                objectMapper = new ObjectMapper();
        }

        @Test
        void getUsers_ShouldReturnAllUsers() throws Exception {
                // Arrange
                UserResponse.UserItem user1 = new UserResponse.UserItem();
                user1.setUserName("user1");
                UserResponse.UserItem user2 = new UserResponse.UserItem();
                user2.setUserName("user2");

                UserResponse userResponse = new UserResponse();
                userResponse.setUsers(Arrays.asList(user1, user2));

                when(userService.getUsers()).thenReturn(BaseResponse.success(userResponse));

                // Act & Assert
                mockMvc.perform(get("/auth/users")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.users").isArray())
                                .andExpect(jsonPath("$.data.users[0].user_name").value("user1"))
                                .andExpect(jsonPath("$.data.users[1].user_name").value("user2"));
        }

        @Test
        void getMe_ShouldReturnCurrentUser() throws Exception {
                // Arrange
                UserResponse.UserItem currentUser = new UserResponse.UserItem();
                currentUser.setUserName("currentUser");
                currentUser.setPermissions(Arrays.asList("READ", "WRITE"));

                when(userService.getMe()).thenReturn(BaseResponse.success(currentUser));

                // Act & Assert
                mockMvc.perform(get("/auth/users/me")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.user_name").value("currentUser"))
                                .andExpect(jsonPath("$.data.permissions").isArray())
                                .andExpect(jsonPath("$.data.permissions[0]").value("READ"));
        }

        @Test
        void getUserById_ShouldReturnUser() throws Exception {
                // Arrange
                String userId = "1";
                UserResponse.UserItem user = new UserResponse.UserItem();
                user.setUserName("user1");

                when(userService.getUserById(anyString())).thenReturn(BaseResponse.success(user));

                // Act & Assert
                mockMvc.perform(get("/auth/users/{id}", userId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.user_name").value("user1"));
        }

        @Test
        void createUser_ShouldReturnCreatedUser() throws Exception {
                // Arrange
                CreateUserRequest request = new CreateUserRequest();
                request.setUserName("newuser");
                request.setPassword("password123");
                request.setEmail("test@example.com");
                request.setFullName("Test User");
                request.setPhoneNumber("1234567890");
                request.setAddress("Test Address");
                request.setCompany("Test Company");

                UserItemResponse userItemResponse = new UserItemResponse();
                userItemResponse.setUserName("newuser");

                when(userService.createUser(any(CreateUserRequest.class)))
                                .thenReturn(BaseResponse.success(userItemResponse));

                // Act & Assert
                mockMvc.perform(post("/auth/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.user_name").value("newuser"));

                verify(userService).createUser(any(CreateUserRequest.class));
        }

        @Test
        void updateUser_ShouldReturnUpdatedUser() throws Exception {
                // Arrange
                String userId = "1";
                UpdateUserRequest request = new UpdateUserRequest();
                request.setFullName("Updated Name");

                UserItemResponse userItemResponse = new UserItemResponse();
                userItemResponse.setUserName("user1");
                userItemResponse.setEmail("updated@example.com");

                when(userService.updateUser(anyString(), any(UpdateUserRequest.class)))
                                .thenReturn(BaseResponse.success(userItemResponse));

                // Act & Assert
                mockMvc.perform(put("/auth/users/{id}", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.user_name").value("user1"));

                verify(userService).updateUser(anyString(), any(UpdateUserRequest.class));
        }

        @Test
        void deleteUser_ShouldReturnSuccess() throws Exception {
                // Arrange
                String userId = "1";
                when(userService.deleteUser(anyString()))
                                .thenReturn(BaseResponse.success(Map.of("id", userId)));

                // Act & Assert
                mockMvc.perform(delete("/auth/users/{id}", userId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.id").value(userId));

                verify(userService).deleteUser(anyString());
        }
}
