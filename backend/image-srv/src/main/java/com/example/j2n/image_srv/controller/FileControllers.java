package com.example.j2n.image_srv.controller;

import com.example.j2n.image_srv.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/file")
@RequiredArgsConstructor
public class FileControllers {
    private final ImageService imageService;

    @GetMapping("/curriculum-vitae/{year}")
    @Operation(summary = "Get static file", description = "Get static file")
    public ResponseEntity<InputStreamResource> getStaticFile(@PathVariable String year) {
        return imageService.getStaticFile(year);
    }
}
