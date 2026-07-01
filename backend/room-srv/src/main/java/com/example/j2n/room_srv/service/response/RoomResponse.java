package com.example.j2n.room_srv.service.response;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RoomResponse extends SimpleRoomResponse {

    @Schema(description = "List of assets in the room")
    private List<AssetResponse> assets;

    @Schema(description = "List of fees associated with the room")
    private List<RoomFeeResponse> fees;

    @Schema(description = "List of members in the room")
    private List<RoomMemberResponse> members;
}

