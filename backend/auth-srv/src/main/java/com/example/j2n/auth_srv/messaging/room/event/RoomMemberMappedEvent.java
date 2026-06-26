package com.example.j2n.auth_srv.messaging.room.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomMemberMappedEvent {
    private Long roomId;
    private List<Long> userIds;
}
