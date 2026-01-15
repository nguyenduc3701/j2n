package com.example.j2n.image_srv.controller;

import com.example.j2n.image_srv.controller.request.UploadImageRequest;
import com.example.j2n.image_srv.service.ImageService;
import com.example.j2n.image_srv.service.response.ImageItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.j2n.dto.BaseResponse;
import java.util.List;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageControllers {
    private final ImageService imageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload image", description = "Upload image")
    public BaseResponse<List<ImageItemResponse>> uploadImage(UploadImageRequest request) {
        return imageService.uploadImage(request);
    }

    @GetMapping("{ownerType}/{id}")
    @Operation(summary = "Get image by id", description = "Get image by id")
    public ResponseEntity<InputStreamResource> getImageById(@PathVariable String ownerType, @PathVariable Long id) {
        return imageService.getImageResource(ownerType, id);
    }
}
