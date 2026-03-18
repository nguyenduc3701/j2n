package com.example.j2n.image_srv.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UploadImageRequest {
    @Schema(description = "Type of owner (e.g., USER, PRODUCT)", example = "USER")
    private Optional<String> ownerType;
    @Schema(description = "ID of the owner", example = "1")
    private Long ownerId;
    @Schema(description = "List of image files to upload")
    private List<MultipartFile> files;
}
