package com.example.j2n.image_srv.controller.request;

import com.example.j2n.dto.BaseRequest;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Request to upload images")
public class UploadImageRequest extends BaseRequest {

    @Builder.Default
    @Schema(description = "Type of owner (e.g., USER, PRODUCT, ROOM)", example = "USER")
    private Optional<String> ownerType = Optional.empty();

    @Schema(description = "ID of the owner", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Owner id is required")
    private Long ownerId;

    @Schema(description = "List of image files to upload", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Files are required")
    private List<MultipartFile> files;

    @Builder.Default
    @Schema(description = "Is primary image", example = "true")
    private Optional<Boolean> isPrimary = Optional.empty();
}
