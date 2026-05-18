package com.example.j2n.room_srv.controller.request;

import com.example.j2n.validation.ValidNotEmpty;
import com.example.j2n.validation.ValidatableRequest;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@ValidNotEmpty
@Schema(description = "Request object to update room member attributes")
public class UpdateRoomMemberRequest implements ValidatableRequest {

    @Schema(description = "Whether the user is the primary member of the room", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Builder.Default
    private Optional<Boolean> isPrimary = Optional.empty();

    @Override
    public boolean isEmpty() {
        return isPrimary.isEmpty();
    }
}
