package com.example.j2n.room_srv.messaging.room.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomStatusUpdatedEvent {
    @Schema(description = "Room ID", example = "1")
    private Long roomId;

    @Schema(description = "Old status of the room", example = "AVAILABLE")
    private String oldStatus;

    @Schema(description = "New status of the room", example = "OCCUPIED")
    private String newStatus;
}
