package com.example.j2n.image_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.image_srv.dto.request.UploadImageRequest;
import com.example.j2n.image_srv.service.ImageService;
import com.example.j2n.image_srv.service.response.ImageItemResponse;
import com.example.j2n.utils.ResponseFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ImageControllersTest {

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ImageControllers imageControllers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadImage_Success() {
        UploadImageRequest request = UploadImageRequest.builder()
                .files(List.of(new MockMultipartFile("file", "test.jpg", "image/jpeg", "test content".getBytes())))
                .build();
        ImageItemResponse responseItem = new ImageItemResponse();
        responseItem.setId(1L);
        List<ImageItemResponse> expected = List.of(responseItem);

        when(imageService.uploadImage(any(UploadImageRequest.class))).thenReturn(ResponseFactory.success(expected));

        BaseResponse<List<ImageItemResponse>> result = imageControllers.uploadImage(request);

        assertEquals(expected, result.getData());
    }

    @Test
    void getImageById_Success() {
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream("test content".getBytes()));
        ResponseEntity<InputStreamResource> expected = ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);

        when(imageService.getImageResource(anyString(), anyLong())).thenReturn(expected);

        ResponseEntity<InputStreamResource> result = imageControllers.getImageById("USER", 1L);

        assertEquals(expected, result);
    }
}
