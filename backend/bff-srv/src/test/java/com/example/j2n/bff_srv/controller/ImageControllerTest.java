package com.example.j2n.bff_srv.controller;

import com.example.j2n.bff_srv.controller.request.UploadImageRequest;
import com.example.j2n.bff_srv.service.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
@AutoConfigureMockMvc(addFilters = false)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @Test
    void uploadImage_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "test.jpg", MediaType.IMAGE_JPEG_VALUE,
                "test content".getBytes());

        when(imageService.uploadImage(any(UploadImageRequest.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(multipart("/api/bff/image/upload")
                .file(file)
                .param("ownerId", "1")
                .param("ownerType", "USER"))
                .andExpect(status().isOk());
    }

    @Test
    void getImageById_Success() throws Exception {
        byte[] content = "test image content".getBytes();
        ResponseEntity<byte[]> responseEntity = ResponseEntity.ok(content);

        when(imageService.getImageResource(anyString(), anyString())).thenReturn(responseEntity);

        mockMvc.perform(get("/api/bff/image/USER/1"))
                .andExpect(status().isOk());
    }

    @Test
    void downloadFile_Success() throws Exception {
        byte[] content = "test file content".getBytes();
        ResponseEntity<byte[]> responseEntity = ResponseEntity.ok(content);

        when(imageService.downloadFile(anyString())).thenReturn(responseEntity);

        mockMvc.perform(get("/api/bff/image/curriculum-vitae/2024/download"))
                .andExpect(status().isOk());
    }
}
