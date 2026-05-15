package com.example.j2n.bff_srv.controller.request;

import com.example.j2n.dto.PagingRequest;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SearchAssetsRequest extends PagingRequest {

    @Schema(description = "Filter by asset name", example = "Air Conditioner", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Optional<String> name = Optional.empty();

    @Schema(description = "Filter by description keyword", example = "Inverter", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Optional<String> description = Optional.empty();
}
