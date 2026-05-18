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
public class UpdateAssetRequest implements ValidatableRequest {

    @Schema(description = "Updated name of the asset", example = "Modern Water Heater", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Builder.Default
    private Optional<String> name = Optional.empty();

    @Schema(description = "Updated description of the asset", example = "Updated capacity to 50L", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Builder.Default
    private Optional<String> description = Optional.empty();

    @Schema(description = "Updated quantity of the asset", example = "2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Builder.Default
    private Optional<Integer> quantity = Optional.empty();

    @Override
    public boolean isEmpty() {
        return name.isEmpty() && description.isEmpty() && quantity.isEmpty();
    }
}
