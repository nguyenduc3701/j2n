package com.example.j2n.notification_srv.service.response;

import com.example.j2n.dto.PagingResponse;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SearchNotificationsResponse {

    @Schema(description = "List of notifications")
    private List<NotificationResponse> notifications;

    @Schema(description = "Paging information")
    private PagingResponse page;

    @Schema(description = "Number of unread notifications")
    private Long unreadCount;
}
