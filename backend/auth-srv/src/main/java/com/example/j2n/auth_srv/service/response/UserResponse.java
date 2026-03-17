package com.example.j2n.auth_srv.service.response;

import java.time.LocalDate;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

import com.example.j2n.dto.PagingResponse;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserResponse {
    @Schema(description = "List of users")
    private List<UserItem> users;

    @Schema(description = "Paging information", example = "{\"total\": 100, \"current\": 0, \"size\": 10}")
    private PagingResponse page;

    @Getter
    @Setter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UserItem {
        @Schema(example = "1")
        private Long id;
        @Schema(example = "admin")
        private String userName;
        @Schema(example = "Administrator")
        private String fullName;
        @Schema(example = "admin@example.com")
        private String email;
        @Schema(example = "0123456789")
        private String phoneNumber;
        @Schema(example = "1990-01-01")
        private LocalDate birth;
        @Schema(example = "https://example.com/image.png")
        private String imageUrl;
        @Schema(example = "room-001")
        private String roomId;
        @Schema(example = "123 Street, City")
        private String address;
        @Schema(example = "Example Corp")
        private String company;
        @Schema(example = "ROLE_ADMIN")
        private String roleId;
        @Schema(example = "ACTIVE")
        private String status;
        @Schema(example = "2024-01-01T00:00:00Z")
        private String createdAt;
        @Schema(example = "2024-01-01T00:00:00Z")
        private String updatedAt;
        @Schema(example = "[\"CAN_VIEW_MANAGEMENT_PAGE\", \"CAN_CREATE_USER\"]")
        private List<String> permissions;
    }
}
