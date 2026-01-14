package com.example.j2n.image_srv.controller;

import com.example.j2n.image_srv.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class FileControllersTest {

    @Mock
    private ImageService imageService;

    @InjectMocks
    private FileControllers fileControllers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getStaticFile_Success() {
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream("pdf content".getBytes()));
        ResponseEntity<InputStreamResource> expected = ResponseEntity.ok().body(resource);

        when(imageService.getStaticFile(anyString())).thenReturn(expected);

        ResponseEntity<InputStreamResource> result = fileControllers.getStaticFile("2025");

        assertEquals(expected, result);
    }
}
