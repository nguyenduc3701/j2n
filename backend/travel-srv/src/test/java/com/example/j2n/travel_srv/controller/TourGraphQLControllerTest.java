package com.example.j2n.travel_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.travel_srv.dto.TourDto;
import com.example.j2n.travel_srv.repository.entity.CategoryEntity;
import com.example.j2n.travel_srv.repository.entity.TourEntity;
import com.example.j2n.travel_srv.service.TourService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourGraphQLControllerTest {

    @Mock
    private TourService tourService;

    @InjectMocks
    private TourGraphQLController tourGraphQLController;

    private TourEntity mockTour;
    private TourDto mockDto;
    private BaseResponse<TourEntity> mockResponse;

    @BeforeEach
    void setUp() {
        CategoryEntity mockCategory = CategoryEntity.builder()
                .id(1L)
                .name("Du lịch biển")
                .slug("du-lich-bien")
                .isDeleted(false)
                .build();

        mockTour = TourEntity.builder()
                .id(1L)
                .category(mockCategory)
                .title("Tour Nha Trang 3N2Đ")
                .description("Khám phá Nha Trang")
                .price(new BigDecimal("2500000.00"))
                .thumbnail("thumbnail.jpg")
                .duration("3 ngày 2 đêm")
                .startLocation("Hà Nội")
                .isDeleted(false)
                .build();

        mockDto = TourDto.builder()
                .categoryId(1L)
                .title("Tour Nha Trang 3N2Đ")
                .description("Khám phá Nha Trang")
                .price(new BigDecimal("2500000.00"))
                .thumbnail("thumbnail.jpg")
                .duration("3 ngày 2 đêm")
                .startLocation("Hà Nội")
                .build();

        mockResponse = new BaseResponse<>();
        mockResponse.setData(mockTour);
    }

    @Test
    void getAllTours_CallsService() {
        BaseResponse<List<TourEntity>> listResponse = new BaseResponse<>();
        listResponse.setData(Collections.singletonList(mockTour));
        when(tourService.getAllTours()).thenReturn(listResponse);

        BaseResponse<List<TourEntity>> result = tourGraphQLController.getAllTours();

        assertEquals(listResponse, result);
        verify(tourService).getAllTours();
    }

    @Test
    void getTourById_CallsService() {
        when(tourService.getTourById(1L)).thenReturn(mockResponse);

        BaseResponse<TourEntity> result = tourGraphQLController.getTourById(1L);

        assertEquals(mockResponse, result);
        verify(tourService).getTourById(1L);
    }

    @Test
    void getToursByCategory_CallsService() {
        BaseResponse<List<TourEntity>> listResponse = new BaseResponse<>();
        listResponse.setData(Collections.singletonList(mockTour));
        when(tourService.getToursByCategory(1L)).thenReturn(listResponse);

        BaseResponse<List<TourEntity>> result = tourGraphQLController.getToursByCategory(1L);

        assertEquals(listResponse, result);
        verify(tourService).getToursByCategory(1L);
    }

    @Test
    void createTour_CallsService() {
        when(tourService.createTour(mockDto)).thenReturn(mockResponse);

        BaseResponse<TourEntity> result = tourGraphQLController.createTour(mockDto);

        assertEquals(mockResponse, result);
        verify(tourService).createTour(mockDto);
    }

    @Test
    void updateTour_CallsService() {
        when(tourService.updateTour(1L, mockDto)).thenReturn(mockResponse);

        BaseResponse<TourEntity> result = tourGraphQLController.updateTour(1L, mockDto);

        assertEquals(mockResponse, result);
        verify(tourService).updateTour(1L, mockDto);
    }

    @Test
    void deleteTour_CallsService() {
        BaseResponse<Boolean> boolResponse = new BaseResponse<>();
        boolResponse.setData(true);
        when(tourService.deleteTour(1L)).thenReturn(boolResponse);

        BaseResponse<Boolean> result = tourGraphQLController.deleteTour(1L);

        assertEquals(boolResponse, result);
        verify(tourService).deleteTour(1L);
    }
}
