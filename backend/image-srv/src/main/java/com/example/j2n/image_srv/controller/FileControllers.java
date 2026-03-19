package com.example.j2n.image_srv.controller;

import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.image_srv.constant.MessageEnum;
import com.example.j2n.image_srv.service.ImageService;

import com.example.j2n.swagger.annotation.J2NApiExample;
import com.example.j2n.swagger.annotation.J2NApiResponse;
import com.example.j2n.swagger.annotation.J2NApiResponses;
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
    @Operation(summary = "Get static file", description = "Get static file (e.g., CV PDF)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = BaseMessageEnum.BaseMessageConstants.SUCCESS, content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE, schema = @Schema(type = "string", format = "binary")))
    })
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 404, description = BaseMessageEnum.BaseMessageConstants.NOT_FOUND_GENERIC, examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.CAN_NOT_READ_STATIC_FILE)
            })
    })
    public ResponseEntity<InputStreamResource> getStaticFile(@PathVariable String year) {
        return imageService.getStaticFile(year);
    }
}
