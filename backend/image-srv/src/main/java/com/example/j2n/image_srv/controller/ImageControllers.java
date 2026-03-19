package com.example.j2n.image_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.image_srv.constant.MessageEnum;
import com.example.j2n.image_srv.controller.request.UploadImageRequest;
import com.example.j2n.image_srv.service.ImageService;
import com.example.j2n.image_srv.service.response.ImageItemResponse;
import com.example.j2n.swagger.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageControllers {
    private final ImageService imageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload image", description = "Upload image files and associate them with an owner")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            }),
            @J2NApiResponse(httpCode = 400, description = BaseMessageEnum.BaseMessageConstants.BAD_REQUEST_GENERIC, examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.FILE_SIZE_TOO_LARGE),
                    @J2NApiExample(status = MessageEnum.MessageConstants.INVALID_FILE_TYPE),
                    @J2NApiExample(status = MessageEnum.MessageConstants.INVALID_OWNER_TYPE),
                    @J2NApiExample(status = BaseMessageEnum.BaseMessageConstants.FIELD_REQUIRED),
                    @J2NApiExample(status = BaseMessageEnum.BaseMessageConstants.FIELD_REQUIRED)
            })
    })
    public BaseResponse<List<ImageItemResponse>> uploadImage(@Valid UploadImageRequest request) {
        return imageService.uploadImage(request);
    }

    @GetMapping("{ownerType}/{id}")
    @Operation(summary = "Get image by id", description = "Retrieve image binary data by owner type and ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = BaseMessageEnum.BaseMessageConstants.SUCCESS, content = @Content(mediaType = "image/*", schema = @Schema(type = "string", format = "binary")))
    })
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 400, description = BaseMessageEnum.BaseMessageConstants.BAD_REQUEST_GENERIC, examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.INVALID_OWNER_TYPE)
            }),
            @J2NApiResponse(httpCode = 404, description = BaseMessageEnum.BaseMessageConstants.NOT_FOUND_GENERIC, examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.IMAGE_NOT_FOUND)
            })
    })
    public ResponseEntity<InputStreamResource> getImageById(@PathVariable String ownerType, @PathVariable Long id) {
        return imageService.getImageResource(ownerType, id);
    }
}
