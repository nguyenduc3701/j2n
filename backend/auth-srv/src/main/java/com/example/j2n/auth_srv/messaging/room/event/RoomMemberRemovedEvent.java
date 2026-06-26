package com.example.j2n.auth_srv.messaging.room.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomMemberRemovedEvent {
    private Long roomId;
    private Long userId;
    private String roomNumber;
}
