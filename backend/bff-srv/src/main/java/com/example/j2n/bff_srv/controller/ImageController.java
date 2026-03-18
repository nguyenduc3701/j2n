package com.example.j2n.bff_srv.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.j2n.bff_srv.service.ImageService;

import io.swagger.v3.oas.annotations.Operation;
import com.example.j2n.swagger.annotation.*;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.bff_srv.controller.request.UploadImageRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/bff/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload image", description = "Upload a new image file")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> uploadImage(UploadImageRequest request) {
        return ResponseEntity.ok(imageService.uploadImage(request));
    }

    @GetMapping("{ownerType}/{id}")
    @Operation(summary = "Get image by id", description = "Retrieve image binary data by owner type and ID")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success")
    })
    public ResponseEntity<byte[]> getImageById(@PathVariable String ownerType, @PathVariable String id) {
        return imageService.getImageResource(ownerType, id);
    }

    @GetMapping("/curriculum-vitae/{year}/download")
    @Operation(summary = "Download file", description = "Download file")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String year) {
        return imageService.downloadFile(year);
    }
}
