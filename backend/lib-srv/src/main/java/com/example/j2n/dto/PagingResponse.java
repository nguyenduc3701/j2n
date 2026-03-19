package com.example.j2n.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard paging information in response")
public class PagingResponse {
    @Schema(description = "Total number of items", example = "100")
    private int total;

    @Schema(description = "Current page number", example = "0")
    private int current;

    @Schema(description = "Size of the current page", example = "50")
    private int size;
}
