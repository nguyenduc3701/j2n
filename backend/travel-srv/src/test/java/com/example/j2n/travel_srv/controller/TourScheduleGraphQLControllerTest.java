package com.example.j2n.travel_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.travel_srv.dto.TourScheduleDto;
import com.example.j2n.travel_srv.repository.entity.TourScheduleEntity;
import com.example.j2n.travel_srv.service.TourScheduleService;
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
class TourScheduleGraphQLControllerTest {

    @Mock
    private TourScheduleService tourScheduleService;

    @InjectMocks
    private TourScheduleGraphQLController tourScheduleGraphQLController;

    private TourScheduleEntity mockSchedule;
    private TourScheduleDto mockDto;
    private BaseResponse<TourScheduleEntity> mockResponse;

    @BeforeEach
    void setUp() {
        mockSchedule = TourScheduleEntity.builder()
                .id(1L)
                .dayNumber(1)
                .title("Ngày 1")
                .content("Nội dung")
                .isDeleted(false)
                .build();

        mockDto = TourScheduleDto.builder()
                .tourId(1L)
                .dayNumber(1)
                .title("Ngày 1")
                .content("Nội dung")
                .build();

        mockResponse = new BaseResponse<>();
        mockResponse.setData(mockSchedule);
    }

    @Test
    void getSchedulesByTourId_CallsService() {
        BaseResponse<List<TourScheduleEntity>> listResponse = new BaseResponse<>();
        listResponse.setData(Collections.singletonList(mockSchedule));
        when(tourScheduleService.getSchedulesByTourId(1L)).thenReturn(listResponse);

        BaseResponse<List<TourScheduleEntity>> result = tourScheduleGraphQLController.getSchedulesByTourId(1L);

        assertEquals(listResponse, result);
        verify(tourScheduleService).getSchedulesByTourId(1L);
    }

    @Test
    void addScheduleToTour_CallsService() {
        when(tourScheduleService.addScheduleToTour(mockDto)).thenReturn(mockResponse);

        BaseResponse<TourScheduleEntity> result = tourScheduleGraphQLController.addScheduleToTour(mockDto);

        assertEquals(mockResponse, result);
        verify(tourScheduleService).addScheduleToTour(mockDto);
    }

    @Test
    void updateTourSchedule_CallsService() {
        when(tourScheduleService.updateTourSchedule(1L, mockDto)).thenReturn(mockResponse);

        BaseResponse<TourScheduleEntity> result = tourScheduleGraphQLController.updateTourSchedule(1L, mockDto);

        assertEquals(mockResponse, result);
        verify(tourScheduleService).updateTourSchedule(1L, mockDto);
    }

    @Test
    void deleteTourSchedule_CallsService() {
        BaseResponse<Boolean> boolResponse = new BaseResponse<>();
        boolResponse.setData(true);
        when(tourScheduleService.deleteTourSchedule(1L)).thenReturn(boolResponse);

        BaseResponse<Boolean> result = tourScheduleGraphQLController.deleteTourSchedule(1L);

        assertEquals(boolResponse, result);
        verify(tourScheduleService).deleteTourSchedule(1L);
    }
}
