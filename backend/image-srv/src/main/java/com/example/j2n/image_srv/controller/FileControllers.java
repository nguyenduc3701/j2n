package com.example.j2n.image_srv.controller;

import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.image_srv.service.ImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/image/file")
@RequiredArgsConstructor
public class FileControllers {
        private final ImageService imageService;

        @GetMapping("/curriculum-vitae/{year}")
        @Operation(summary = "Get static file", description = "Get static file")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = BaseMessageEnum.BaseMessageConstants.SUCCESS, content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE, schema = @Schema(type = "string", format = "binary"))),
                        @ApiResponse(responseCode = "400", description = BaseMessageEnum.BaseMessageConstants.BAD_REQUEST)
        })
        public ResponseEntity<InputStreamResource> getStaticFile(@PathVariable String year) {
                return imageService.getStaticFile(year);
        }
}
