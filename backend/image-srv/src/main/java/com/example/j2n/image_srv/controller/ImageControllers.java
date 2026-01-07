package com.example.j2n.image_srv.controller;

import com.example.j2n.image_srv.dto.request.UploadImageRequest;
import com.example.j2n.image_srv.repository.entity.ImageEntity;
import com.example.j2n.image_srv.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/image")
@RequiredArgsConstructor
public class ImageControllers {
    private final ImageService imageService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(summary = "Upload image", description = "Upload image")
    public ImageEntity uploadImage(UploadImageRequest request) {
        return imageService.uploadImage(
                request.getOwnerType(),
                request.getOwnerId(),
                request.getImageType(),
                request.getFile());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get image by id", description = "Get image by id")
    public ResponseEntity<InputStreamResource> getImageById(@PathVariable Long id) {
        return imageService.getImageResource(id);
    }
}
