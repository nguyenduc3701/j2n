package com.example.j2n.image_srv.service.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImageItemResponse {
    @Schema(description = "ID of the image", example = "1")
    private Long id;
    @Schema(description = "Relative path to the image file", example = "uploads/2026/03/18/image.png")
    private String filePath;
}
