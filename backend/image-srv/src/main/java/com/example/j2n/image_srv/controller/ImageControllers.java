package com.example.j2n.image_srv.controller;

import com.example.j2n.image_srv.repository.entity.ImageEntity;
import com.example.j2n.image_srv.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/image")
@RequiredArgsConstructor
public class ImageControllers {
    private final ImageService imageService;

    @PostMapping("/upload")
    @Operation(summary = "Upload image", description = "Upload image")
    public ImageEntity uploadImage(
            @RequestParam String ownerType,
            @RequestParam Long ownerId,
            @RequestParam(required = false) String imageType,
            @RequestParam("file") MultipartFile file) {
        return imageService.uploadImage(ownerType, ownerId, imageType, file);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get image by id", description = "Get image by id")
    public ResponseEntity<InputStreamResource> getImageById(@PathVariable Long id) {
        return imageService.getImageResource(id);
    }
}
