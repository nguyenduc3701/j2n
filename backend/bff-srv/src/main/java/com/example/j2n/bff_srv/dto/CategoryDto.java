package com.example.j2n.bff_srv.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Category data transfer object for creation and update")
public class CategoryDto {
    @Schema(description = "Category name", example = "Adventure")
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @Schema(description = "Category slug", example = "adventure")
    @NotBlank(message = "Slug is required")
    private String slug;
}
