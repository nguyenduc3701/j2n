package com.example.j2n.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Base response wrapper for all API responses")
public class BaseResponse<T> {
    @Schema(description = "Response status code", example = "200")
    private String code;

    @Schema(description = "Response message", example = "Success")
    private String message;

    @Schema(description = "Response payload data")
    private T data;
}
