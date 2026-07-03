package com.example.j2n.notification_srv.service.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NotificationResponse {

    @Schema(description = "Notification ID", example = "1")
    private Long id;

    @Schema(description = "User ID", example = "user-123")
    private String userId;

    @Schema(description = "Notification type", example = "BILL_CREATED")
    private String type;

    @Schema(description = "Notification title", example = "Room Bill 101 - Month 5")
    private String title;

    @Schema(description = "Notification message", example = "Total amount: 2,500,000 VND")
    private String message;

    @Schema(description = "Read status", example = "false")
    private Boolean isRead;

    @Schema(description = "Creation timestamp", example = "2024-05-11T10:00:00")
    private LocalDateTime createdAt;
}
