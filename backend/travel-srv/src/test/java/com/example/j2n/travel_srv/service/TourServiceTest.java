package com.example.j2n.travel_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.travel_srv.dto.TourDto;
import com.example.j2n.travel_srv.repository.TourRepository;
import com.example.j2n.travel_srv.repository.entity.CategoryEntity;
import com.example.j2n.travel_srv.repository.entity.TourEntity;
import com.example.j2n.travel_srv.messaging.travel.event.TourCreatedEvent;
import com.example.j2n.travel_srv.messaging.travel.event.TourDeletedEvent;
import com.example.j2n.travel_srv.messaging.travel.publisher.TourEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourServiceTest {

    @Mock
    private TourRepository tourRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private TourEventPublisher tourEventPublisher;

    @InjectMocks
    private TourService tourService;

    private CategoryEntity mockCategory;
    private TourEntity mockTour;
    private TourDto mockDto;

    @BeforeEach
    void setUp() {
        mockCategory = CategoryEntity.builder()
                .id(1L)
                .name("Du lịch biển")
                .slug("du-lich-bien")
                .isDeleted(false)
                .build();

        mockTour = TourEntity.builder()
                .id(1L)
                .category(mockCategory)
                .title("Tour Nha Trang 3N2Đ")
                .description("Khám phá Nha Trang tuyệt vời")
                .price(new BigDecimal("2500000.00"))
                .thumbnail("thumbnail.jpg")
                .duration("3 ngày 2 đêm")
                .startLocation("Hà Nội")
                .isDeleted(false)
                .build();

        mockDto = TourDto.builder()
                .categoryId(1L)
                .title("Tour Nha Trang 3N2Đ")
                .description("Khám phá Nha Trang tuyệt vời")
                .price(new BigDecimal("2500000.00"))
                .thumbnail("thumbnail.jpg")
                .duration("3 ngày 2 đêm")
                .startLocation("Hà Nội")
                .build();
    }

    // ===================== getAllTours =====================

    @Test
    void getAllTours_Success() {
        when(tourRepository.findAllByIsDeletedFalse()).thenReturn(Collections.singletonList(mockTour));

        BaseResponse<List<TourEntity>> response = tourService.getAllTours();

        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        verify(tourRepository, times(1)).findAllByIsDeletedFalse();
    }

    // ===================== getTourById =====================

    @Test
    void getTourById_Success() {
        when(tourRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockTour));

        BaseResponse<TourEntity> response = tourService.getTourById(1L);

        assertNotNull(response.getData());
        assertEquals(mockTour.getTitle(), response.getData().getTitle());
        verify(tourRepository, times(1)).findByIdAndIsDeletedFalse(1L);
    }

    @Test
    void getTourById_Fail_NotFound() {
        when(tourRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> tourService.getTourById(1L));
    }

    @Test
    void getTourById_Fail_InvalidId_Null() {
        assertThrows(InvalidInputException.class, () -> tourService.getTourById(null));
    }

    @Test
    void getTourById_Fail_InvalidId_Zero() {
        assertThrows(InvalidInputException.class, () -> tourService.getTourById(0L));
    }

    @Test
    void getTourById_Fail_InvalidId_Negative() {
        assertThrows(InvalidInputException.class, () -> tourService.getTourById(-5L));
    }

    // ===================== getToursByCategory =====================

    @Test
    void getToursByCategory_Success() {
        when(tourRepository.findByCategoryIdAndIsDeletedFalse(1L)).thenReturn(Collections.singletonList(mockTour));

        BaseResponse<List<TourEntity>> response = tourService.getToursByCategory(1L);

        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        verify(tourRepository, times(1)).findByCategoryIdAndIsDeletedFalse(1L);
    }

    @Test
    void getToursByCategory_Fail_InvalidId_Null() {
        assertThrows(InvalidInputException.class, () -> tourService.getToursByCategory(null));
    }

    @Test
    void getToursByCategory_Fail_InvalidId_Zero() {
        assertThrows(InvalidInputException.class, () -> tourService.getToursByCategory(0L));
    }

    @Test
    void getToursByCategory_Fail_InvalidId_Negative() {
        assertThrows(InvalidInputException.class, () -> tourService.getToursByCategory(-1L));
    }

    // ===================== createTour =====================

    @Test
    void createTour_Success() {
        when(categoryService.getCategoryByIdOrThrow(1L)).thenReturn(mockCategory);
        when(tourRepository.save(any(TourEntity.class))).thenReturn(mockTour);

        BaseResponse<TourEntity> response = tourService.createTour(mockDto);

        assertNotNull(response.getData());
        assertEquals(mockTour.getTitle(), response.getData().getTitle());
        verify(categoryService, times(1)).getCategoryByIdOrThrow(1L);
        verify(tourRepository, times(1)).save(any(TourEntity.class));
        verify(tourEventPublisher, times(1)).publishTourCreated(any(TourCreatedEvent.class));
    }

    @Test
    void createTour_Fail_CategoryNotFound() {
        when(categoryService.getCategoryByIdOrThrow(1L)).thenThrow(new DataNotFoundException(null));

        assertThrows(DataNotFoundException.class, () -> tourService.createTour(mockDto));
        verify(tourRepository, never()).save(any());
    }

    @Test
    void createTour_Fail_InvalidCategoryId_Null() {
        TourDto dto = TourDto.builder()
                .categoryId(null)
                .title("Tour test")
                .price(new BigDecimal("1000000"))
                .build();

        assertThrows(InvalidInputException.class, () -> tourService.createTour(dto));
    }

    @Test
    void createTour_Fail_InvalidCategoryId_Zero() {
        TourDto dto = TourDto.builder()
                .categoryId(0L)
                .title("Tour test")
                .price(new BigDecimal("1000000"))
                .build();

        assertThrows(InvalidInputException.class, () -> tourService.createTour(dto));
    }

    // ===================== updateTour =====================

    @Test
    void updateTour_Success() {
        when(tourRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockTour));
        when(categoryService.getCategoryByIdOrThrow(1L)).thenReturn(mockCategory);
        when(tourRepository.save(any(TourEntity.class))).thenReturn(mockTour);

        BaseResponse<TourEntity> response = tourService.updateTour(1L, mockDto);

        assertNotNull(response.getData());
        verify(tourRepository, times(1)).findByIdAndIsDeletedFalse(1L);
        verify(categoryService, times(1)).getCategoryByIdOrThrow(1L);
        verify(tourRepository, times(1)).save(any(TourEntity.class));
    }

    @Test
    void updateTour_Fail_TourNotFound() {
        when(tourRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> tourService.updateTour(1L, mockDto));
        verify(tourRepository, never()).save(any());
    }

    @Test
    void updateTour_Fail_TourIsDeleted() {
        // findByIdAndIsDeletedFalse won't return deleted tours, so simulate via not found
        when(tourRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> tourService.updateTour(1L, mockDto));
    }

    @Test
    void updateTour_Fail_InvalidTourId_Null() {
        assertThrows(InvalidInputException.class, () -> tourService.updateTour(null, mockDto));
    }

    @Test
    void updateTour_Fail_InvalidTourId_Zero() {
        assertThrows(InvalidInputException.class, () -> tourService.updateTour(0L, mockDto));
    }

    @Test
    void updateTour_Fail_CategoryNotFound() {
        when(tourRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockTour));
        when(categoryService.getCategoryByIdOrThrow(1L)).thenThrow(new DataNotFoundException(null));

        assertThrows(DataNotFoundException.class, () -> tourService.updateTour(1L, mockDto));
        verify(tourRepository, never()).save(any());
    }

    @Test
    void updateTour_Fail_InvalidCategoryId_Null() {
        TourDto dto = TourDto.builder().categoryId(null).title("Tour").price(new BigDecimal("1000000")).build();
        when(tourRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockTour));

        assertThrows(InvalidInputException.class, () -> tourService.updateTour(1L, dto));
    }

    // ===================== deleteTour =====================

    @Test
    void deleteTour_Success() {
        when(tourRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockTour));
        when(tourRepository.save(any(TourEntity.class))).thenReturn(mockTour);

        BaseResponse<Boolean> response = tourService.deleteTour(1L);

        assertNull(response.getData());
        assertEquals("true", String.valueOf(mockTour.getIsDeleted()));
        verify(tourRepository, times(1)).save(any(TourEntity.class));
        verify(tourEventPublisher, times(1)).publishTourDeleted(any(TourDeletedEvent.class));
    }

    @Test
    void deleteTour_Fail_TourNotFound() {
        when(tourRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> tourService.deleteTour(1L));
        verify(tourRepository, never()).save(any());
    }

    @Test
    void deleteTour_Fail_InvalidId_Null() {
        assertThrows(InvalidInputException.class, () -> tourService.deleteTour(null));
    }

    @Test
    void deleteTour_Fail_InvalidId_Zero() {
        assertThrows(InvalidInputException.class, () -> tourService.deleteTour(0L));
    }

    @Test
    void deleteTour_Fail_InvalidId_Negative() {
        assertThrows(InvalidInputException.class, () -> tourService.deleteTour(-1L));
    }

    // ===================== validateTour (isDeleted = true via entity mock) =====================

    @Test
    void updateTour_Fail_ValidateTour_IsDeleted_True() {
        TourEntity deletedTour = TourEntity.builder()
                .id(2L)
                .category(mockCategory)
                .title("Tour đã bị xóa")
                .price(new BigDecimal("1000000"))
                .isDeleted(true)
                .build();

        when(tourRepository.findByIdAndIsDeletedFalse(2L)).thenReturn(Optional.of(deletedTour));

        assertThrows(DataNotFoundException.class, () -> tourService.updateTour(2L, mockDto));
    }

    @Test
    void deleteTour_Fail_ValidateTour_IsDeleted_True() {
        TourEntity deletedTour = TourEntity.builder()
                .id(2L)
                .category(mockCategory)
                .title("Tour đã bị xóa")
                .price(new BigDecimal("1000000"))
                .isDeleted(true)
                .build();

        when(tourRepository.findByIdAndIsDeletedFalse(2L)).thenReturn(Optional.of(deletedTour));

        assertThrows(DataNotFoundException.class, () -> tourService.deleteTour(2L));
    }

    @Test
    void publishTourCreatedEvent_NullId_Skips() {
        TourEntity tour = new TourEntity();
        tour.setId(null);
        
        when(categoryService.getCategoryByIdOrThrow(anyLong())).thenReturn(mockCategory);
        when(tourRepository.save(any())).thenReturn(tour);
        
        tourService.createTour(mockDto);
        verify(tourEventPublisher, never()).publishTourCreated(any());
    }

    @Test
    void publishTourDeletedEvent_NullId_Skips() {
        TourEntity tour = new TourEntity();
        tour.setId(null);
        when(tourRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.of(tour));
        
        tourService.deleteTour(1L);
        verify(tourEventPublisher, never()).publishTourDeleted(any());
    }

    @Test
    void publishTourCreatedEvent_NullCreatedAt_UsesNull() {
        mockTour.setCreatedAt(null);
        when(categoryService.getCategoryByIdOrThrow(anyLong())).thenReturn(mockCategory);
        when(tourRepository.save(any())).thenReturn(mockTour);

        tourService.createTour(mockDto);
        verify(tourEventPublisher).publishTourCreated(argThat(event -> event.getCreatedAt() == null));
    }
}
