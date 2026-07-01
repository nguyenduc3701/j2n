package com.example.j2n.room_srv.service.response;

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
@Schema(description = "Data transfer object for room member information")
public class RoomMemberResponse {

    @Schema(description = "ID of the room member mapping", example = "1")
    private Long id;

    @Schema(description = "ID of the room", example = "1")
    private Long roomId;

    @Schema(description = "ID of the user who is a member of the room", example = "10")
    private Long userId;

    @Schema(description = "Whether the user is the primary member/tenant of the room", example = "true")
    private Boolean isPrimary;

    @Schema(description = "Timestamp when the user joined the room")
    private LocalDateTime joinedAt;

    @Schema(description = "Full name of the member", example = "John Doe")
    private String fullName;

    @Schema(description = "Phone number of the member", example = "0987654321")
    private String phoneNumber;

    @Schema(description = "Email of the member", example = "john.doe@example.com")
    private String email;
}

