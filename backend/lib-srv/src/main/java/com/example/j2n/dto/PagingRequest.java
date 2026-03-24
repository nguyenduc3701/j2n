package com.example.j2n.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Standard paging request")
public class PagingRequest extends BaseRequest {
    @Schema(description = "Page number (starting from 1)", example = "1")
    @Min(value = 1, message = "Page must be greater than or equal to 1")
    private Integer page = 1;

    @Schema(description = "Number of items per page", example = "50")
    @Min(value = 1, message = "Size must be greater than or equal to 1")
    private Integer size;

    @Schema(description = "Whether to enable paging", example = "true")
    private Boolean paging = true;

    @Schema(description = "Field to sort by", example = "id")
    private String sortField;

    @Schema(description = "Sort direction", example = "ASC", allowableValues = {"ASC", "DESC"})
    private String sortDirection;
}
