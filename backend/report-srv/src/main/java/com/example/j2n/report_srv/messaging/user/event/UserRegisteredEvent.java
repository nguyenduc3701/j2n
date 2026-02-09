package com.example.j2n.report_srv.messaging.user.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserRegisteredEvent {
    private String userId;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String role;
    private String status;
    private String createdAt;
}
