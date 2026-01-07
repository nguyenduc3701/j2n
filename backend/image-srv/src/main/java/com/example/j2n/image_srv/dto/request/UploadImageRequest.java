package com.example.j2n.image_srv.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadImageRequest {
    private String ownerType;
    private Long ownerId;
    private String imageType;
    private MultipartFile file;
}
