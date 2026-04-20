package com.example.j2n.travel_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.travel_srv.dto.TourImageDto;
import com.example.j2n.travel_srv.repository.TourImageRepository;
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
    private TourService tourService;

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
        when(tourService.getTourByIdOrThrow(1L)).thenReturn(mockTour);
        when(tourImageRepository.save(any(TourImageEntity.class))).thenAnswer(i -> i.getArgument(0));

        BaseResponse<TourImageEntity> response = tourImageService.addImageToTour(mockDto);

        assertNotNull(response.getData());
        verify(tourService, times(1)).getTourByIdOrThrow(1L);
        verify(tourImageRepository, times(1)).save(any(TourImageEntity.class));
    }

    @Test
    void addImageToTour_Success_AsPrimary() {
        mockDto.setIsPrimary(true);
        TourImageEntity currentPrimary = TourImageEntity.builder().id(2L).isPrimary(true).build();
        
        when(tourService.getTourByIdOrThrow(1L)).thenReturn(mockTour);
        when(tourImageRepository.findAllByTourIdAndIsPrimaryTrueAndIsDeletedFalse(1L)).thenReturn(List.of(currentPrimary));
        when(tourImageRepository.save(any(TourImageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tourImageRepository.saveAll(anyList())).thenReturn(List.of(currentPrimary));

        BaseResponse<TourImageEntity> response = tourImageService.addImageToTour(mockDto);

        assertNotNull(response.getData());
        assertTrue(response.getData().getIsPrimary());
        verify(tourImageRepository, times(1)).findAllByTourIdAndIsPrimaryTrueAndIsDeletedFalse(1L);
        verify(tourImageRepository, times(1)).saveAll(anyList());
        assertEquals("http://example.com/image.jpg", mockTour.getThumbnail());
    }

    @Test
    void addImageToTour_Fail_TourNotFound() {
        when(tourService.getTourByIdOrThrow(1L)).thenThrow(new DataNotFoundException(null));

        assertThrows(DataNotFoundException.class, () -> tourImageService.addImageToTour(mockDto));
    }

    @Test
    void deleteTourImage_Success() {
        mockImage.setIsPrimary(true);
        when(tourImageRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockImage));
        when(tourImageRepository.save(any(TourImageEntity.class))).thenReturn(mockImage);
        when(tourImageRepository.findAllByTourIdAndIsPrimaryTrueAndIsDeletedFalse(1L)).thenReturn(Collections.emptyList());

        BaseResponse<Boolean> response = tourImageService.deleteTourImage(1L);

        assertNull(response.getData());
        assertTrue(mockImage.getIsDeleted());
        assertNull(mockTour.getThumbnail());
        verify(tourImageRepository, times(1)).save(mockImage);
    }

    @Test
    void setPrimaryImage_Success() {
        when(tourImageRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockImage));
        when(tourImageRepository.findAllByTourIdAndIsPrimaryTrueAndIsDeletedFalse(1L)).thenReturn(Collections.emptyList());
        when(tourImageRepository.save(any(TourImageEntity.class))).thenReturn(mockImage);
        when(tourImageRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        BaseResponse<TourImageEntity> response = tourImageService.setPrimaryImage(1L);

        assertNotNull(response.getData());
        assertTrue(mockImage.getIsPrimary());
        assertEquals(mockImage.getImageUrl(), mockTour.getThumbnail());
        verify(tourImageRepository, times(1)).save(mockImage);
    }

    @Test
    void setPrimaryImage_Success_UnmarkOld() {
        TourImageEntity currentPrimary = TourImageEntity.builder().id(2L).isPrimary(true).build();
        
        when(tourImageRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockImage));
        when(tourImageRepository.findAllByTourIdAndIsPrimaryTrueAndIsDeletedFalse(1L)).thenReturn(List.of(currentPrimary));
        when(tourImageRepository.save(any(TourImageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tourImageRepository.saveAll(anyList())).thenReturn(List.of(currentPrimary));

        BaseResponse<TourImageEntity> response = tourImageService.setPrimaryImage(1L);

        assertNotNull(response.getData());
        assertTrue(mockImage.getIsPrimary());
        assertFalse(currentPrimary.getIsPrimary());
        verify(tourImageRepository, times(1)).save(any(TourImageEntity.class));
        verify(tourImageRepository, times(1)).saveAll(anyList());
    }

    @Test
    void validateLong_Fail() {
        assertThrows(InvalidInputException.class, () -> tourImageService.getImagesByTourId(null));
        assertThrows(InvalidInputException.class, () -> tourImageService.getImagesByTourId(0L));
        assertThrows(InvalidInputException.class, () -> tourImageService.getImagesByTourId(-1L));
    }
}
