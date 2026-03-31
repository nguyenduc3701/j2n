package com.example.j2n.bff_srv.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Category response data")
public class CategoryResponse {
    @Schema(description = "Category ID")
    private Long id;

    @Schema(description = "Category name")
    private String name;

    @Schema(description = "Category slug")
    private String slug;

    @Schema(description = "Creation date")
    private LocalDateTime createdAt;

    @Schema(description = "Update date")
    private LocalDateTime updatedAt;

    @Schema(description = "Is category deleted")
    private Boolean isDeleted;
}
