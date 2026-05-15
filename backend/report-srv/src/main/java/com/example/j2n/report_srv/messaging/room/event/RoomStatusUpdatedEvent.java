package com.example.j2n.report_srv.messaging.room.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomStatusUpdatedEvent {
    private Long roomId;
    private String oldStatus;
    private String newStatus;
}
