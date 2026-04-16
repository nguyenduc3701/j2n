package com.example.j2n.travel_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.travel_srv.dto.TourImageDto;
import com.example.j2n.travel_srv.repository.entity.TourImageEntity;
import com.example.j2n.travel_srv.service.TourImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourImageGraphQLControllerTest {

    @Mock
    private TourImageService tourImageService;

    @InjectMocks
    private TourImageGraphQLController tourImageGraphQLController;

    private TourImageEntity mockImage;
    private TourImageDto mockDto;
    private BaseResponse<TourImageEntity> mockResponse;

    @BeforeEach
    void setUp() {
        mockImage = TourImageEntity.builder()
                .id(1L)
                .imageUrl("http://example.com/image.jpg")
                .isPrimary(false)
                .isDeleted(false)
                .build();

        mockDto = TourImageDto.builder()
                .tourId(1L)
                .imageUrl("http://example.com/image.jpg")
                .isPrimary(false)
                .build();

        mockResponse = new BaseResponse<>();
        mockResponse.setData(mockImage);
    }

    @Test
    void getImagesByTourId_CallsService() {
        BaseResponse<List<TourImageEntity>> listResponse = new BaseResponse<>();
        listResponse.setData(Collections.singletonList(mockImage));
        when(tourImageService.getImagesByTourId(1L)).thenReturn(listResponse);

        BaseResponse<List<TourImageEntity>> result = tourImageGraphQLController.getImagesByTourId(1L);

        assertEquals(listResponse, result);
        verify(tourImageService).getImagesByTourId(1L);
    }

    @Test
    void addImageToTour_CallsService() {
        when(tourImageService.addImageToTour(mockDto)).thenReturn(mockResponse);

        BaseResponse<TourImageEntity> result = tourImageGraphQLController.addImageToTour(mockDto);

        assertEquals(mockResponse, result);
        verify(tourImageService).addImageToTour(mockDto);
    }

    @Test
    void deleteTourImage_CallsService() {
        BaseResponse<Boolean> boolResponse = new BaseResponse<>();
        boolResponse.setData(true);
        when(tourImageService.deleteTourImage(1L)).thenReturn(boolResponse);

        BaseResponse<Boolean> result = tourImageGraphQLController.deleteTourImage(1L);

        assertEquals(boolResponse, result);
        verify(tourImageService).deleteTourImage(1L);
    }

    @Test
    void setPrimaryImage_CallsService() {
        when(tourImageService.setPrimaryImage(1L)).thenReturn(mockResponse);

        BaseResponse<TourImageEntity> result = tourImageGraphQLController.setPrimaryImage(1L);

        assertEquals(mockResponse, result);
        verify(tourImageService).setPrimaryImage(1L);
    }
}
