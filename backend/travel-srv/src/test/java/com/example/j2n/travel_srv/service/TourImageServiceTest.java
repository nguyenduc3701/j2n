package com.example.j2n.travel_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.travel_srv.dto.TourImageDto;
import com.example.j2n.travel_srv.repository.TourImageRepository;
import com.example.j2n.travel_srv.repository.TourRepository;
import com.example.j2n.travel_srv.repository.entity.TourEntity;
import com.example.j2n.travel_srv.repository.entity.TourImageEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourImageServiceTest {

    @Mock
    private TourImageRepository tourImageRepository;

    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private TourImageService tourImageService;

    private TourEntity mockTour;
    private TourImageEntity mockImage;
    private TourImageDto mockDto;

    @BeforeEach
    void setUp() {
        mockTour = TourEntity.builder()
                .id(1L)
                .title("Tour Test")
                .isDeleted(false)
                .build();

        mockImage = TourImageEntity.builder()
                .id(1L)
                .tour(mockTour)
                .imageUrl("http://example.com/image.jpg")
                .isPrimary(false)
                .isDeleted(false)
                .build();

        mockDto = TourImageDto.builder()
                .tourId(1L)
                .imageUrl("http://example.com/image.jpg")
                .isPrimary(false)
                .build();
    }

    @Test
    void getImagesByTourId_Success() {
        when(tourImageRepository.findAllByTourIdAndIsDeletedFalse(1L)).thenReturn(Collections.singletonList(mockImage));

        BaseResponse<List<TourImageEntity>> response = tourImageService.getImagesByTourId(1L);

        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        verify(tourImageRepository, times(1)).findAllByTourIdAndIsDeletedFalse(1L);
    }

    @Test
    void addImageToTour_Success() {
        when(tourRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockTour));
        when(tourImageRepository.save(any(TourImageEntity.class))).thenReturn(mockImage);

        BaseResponse<TourImageEntity> response = tourImageService.addImageToTour(mockDto);

        assertNotNull(response.getData());
        verify(tourRepository, times(1)).findByIdAndIsDeletedFalse(1L);
        verify(tourImageRepository, times(1)).save(any(TourImageEntity.class));
    }

    @Test
    void addImageToTour_Success_AsPrimary() {
        mockDto.setIsPrimary(true);
        TourImageEntity currentPrimary = TourImageEntity.builder().id(2L).isPrimary(true).build();
        
        when(tourRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockTour));
        when(tourImageRepository.findByTourIdAndIsPrimaryTrueAndIsDeletedFalse(1L)).thenReturn(Optional.of(currentPrimary));
        when(tourImageRepository.save(any(TourImageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BaseResponse<TourImageEntity> response = tourImageService.addImageToTour(mockDto);

        assertNotNull(response.getData());
        assertTrue(response.getData().getIsPrimary());
        verify(tourImageRepository, times(1)).findByTourIdAndIsPrimaryTrueAndIsDeletedFalse(1L);
        verify(tourImageRepository, times(2)).save(any(TourImageEntity.class)); // 1 for unmarking, 1 for saving new
    }

    @Test
    void addImageToTour_Fail_TourNotFound() {
        when(tourRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> tourImageService.addImageToTour(mockDto));
    }

    @Test
    void deleteTourImage_Success() {
        when(tourImageRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockImage));
        when(tourImageRepository.save(any(TourImageEntity.class))).thenReturn(mockImage);

        BaseResponse<Boolean> response = tourImageService.deleteTourImage(1L);

        assertTrue(response.getData());
        assertTrue(mockImage.getIsDeleted());
        verify(tourImageRepository, times(1)).save(mockImage);
    }

    @Test
    void setPrimaryImage_Success() {
        when(tourImageRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockImage));
        when(tourImageRepository.findByTourIdAndIsPrimaryTrueAndIsDeletedFalse(1L)).thenReturn(Optional.empty());
        when(tourImageRepository.save(any(TourImageEntity.class))).thenReturn(mockImage);

        BaseResponse<TourImageEntity> response = tourImageService.setPrimaryImage(1L);

        assertNotNull(response.getData());
        assertTrue(mockImage.getIsPrimary());
        verify(tourImageRepository, times(1)).save(mockImage);
    }

    @Test
    void setPrimaryImage_Success_UnmarkOld() {
        TourImageEntity currentPrimary = TourImageEntity.builder().id(2L).isPrimary(true).build();
        
        when(tourImageRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockImage));
        when(tourImageRepository.findByTourIdAndIsPrimaryTrueAndIsDeletedFalse(1L)).thenReturn(Optional.of(currentPrimary));
        when(tourImageRepository.save(any(TourImageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BaseResponse<TourImageEntity> response = tourImageService.setPrimaryImage(1L);

        assertNotNull(response.getData());
        assertTrue(mockImage.getIsPrimary());
        assertFalse(currentPrimary.getIsPrimary());
        verify(tourImageRepository, times(2)).save(any(TourImageEntity.class));
    }

    @Test
    void validateLong_Fail() {
        assertThrows(InvalidInputException.class, () -> tourImageService.getImagesByTourId(null));
        assertThrows(InvalidInputException.class, () -> tourImageService.getImagesByTourId(0L));
        assertThrows(InvalidInputException.class, () -> tourImageService.getImagesByTourId(-1L));
    }
}
