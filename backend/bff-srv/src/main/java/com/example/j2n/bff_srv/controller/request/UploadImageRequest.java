package com.example.j2n.bff_srv.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UploadImageRequest {
    @Builder.Default
    @Schema(description = "Owner type (e.g., USER, PRODUCT, ROOM, PRODUCT)", example = "USER")
    private Optional<String> ownerType = Optional.empty();

    @Schema(description = "Owner ID", example = "1")
    private Long ownerId;

    @Schema(description = "List of image files to upload")
    private List<MultipartFile> files;

    @Builder.Default
    @Schema(description = "Is primary image", example = "true")
    private Optional<Boolean> isPrimary = Optional.empty();
}
