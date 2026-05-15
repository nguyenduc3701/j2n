package com.example.j2n.room_srv.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing the various statuses a room can have.
 */
@Getter
@AllArgsConstructor
public enum RoomStatus {
    AVAILABLE("AVAILABLE"),
    OCCUPIED("OCCUPIED"),
    MAINTENANCE("MAINTENANCE");

    private final String value;
}
