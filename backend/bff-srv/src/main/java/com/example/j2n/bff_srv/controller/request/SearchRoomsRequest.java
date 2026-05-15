package com.example.j2n.bff_srv.controller.request;

import com.example.j2n.dto.PagingRequest;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SearchRoomsRequest extends PagingRequest {

    @Schema(description = "Filter by room number", example = "101", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Optional<String> roomNumber = Optional.empty();

    @Schema(description = "Filter by floor", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Optional<Integer> floor = Optional.empty();

    @Schema(description = "Filter by status", example = "AVAILABLE", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Optional<String> status = Optional.empty();

    @Schema(description = "Filter by minimum base price", example = "1000000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Optional<BigDecimal> minPrice = Optional.empty();

    @Schema(description = "Filter by maximum base price", example = "5000000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Optional<BigDecimal> maxPrice = Optional.empty();

    @Schema(description = "Filter by area", example = "20m2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Optional<String> area = Optional.empty();

    @Schema(description = "Filter by maximum people", example = "2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Optional<Integer> maxPeople = Optional.empty();
}
