package com.example.j2n.auth_srv.service.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserResponse {
    private List<UserItem> users;
    private PagingResponse page;

    @Getter
    @Setter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UserItem {
        private Long id;
        private String userName;
        private String fullName;
        private String email;
        private String phoneNumber;
        private LocalDate birth;
        private String imageUrl;
        private String roomId;
        private String address;
        private String company;
        private String roleId;
        private String status;
        private String createdAt;
        private String updatedAt;
        private List<String> permissions;
    }
}
