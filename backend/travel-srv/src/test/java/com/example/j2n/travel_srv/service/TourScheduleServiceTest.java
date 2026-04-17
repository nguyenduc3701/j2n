package com.example.j2n.travel_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.travel_srv.dto.TourScheduleDto;
import com.example.j2n.travel_srv.repository.TourScheduleRepository;
import com.example.j2n.travel_srv.repository.TourRepository;
import com.example.j2n.travel_srv.repository.entity.TourEntity;
import com.example.j2n.travel_srv.repository.entity.TourScheduleEntity;
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
class TourScheduleServiceTest {

    @Mock
    private TourScheduleRepository tourScheduleRepository;

    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private TourScheduleService tourScheduleService;

    private TourEntity mockTour;
    private TourScheduleEntity mockSchedule;
    private TourScheduleDto mockDto;

    @BeforeEach
    void setUp() {
        mockTour = TourEntity.builder()
                .id(1L)
                .title("Tour Test")
                .isDeleted(false)
                .build();

        mockSchedule = TourScheduleEntity.builder()
                .id(1L)
                .tour(mockTour)
                .dayNumber(1)
                .title("Ngày 1: Khởi hành")
                .content("Bắt đầu chuyến đi")
                .isDeleted(false)
                .build();

        mockDto = TourScheduleDto.builder()
                .tourId(1L)
                .dayNumber(1)
                .title("Ngày 1: Khởi hành")
                .content("Bắt đầu chuyến đi")
                .build();
    }

    @Test
    void getSchedulesByTourId_Success() {
        when(tourScheduleRepository.findAllByTourIdAndIsDeletedFalseOrderByDayNumberAsc(1L))
                .thenReturn(Collections.singletonList(mockSchedule));

        BaseResponse<List<TourScheduleEntity>> response = tourScheduleService.getSchedulesByTourId(1L);

        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        verify(tourScheduleRepository, times(1)).findAllByTourIdAndIsDeletedFalseOrderByDayNumberAsc(1L);
    }

    @Test
    void addScheduleToTour_Success() {
        when(tourRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockTour));
        when(tourScheduleRepository.save(any(TourScheduleEntity.class))).thenReturn(mockSchedule);

        BaseResponse<TourScheduleEntity> response = tourScheduleService.addScheduleToTour(mockDto);

        assertNotNull(response.getData());
        assertEquals(mockSchedule.getTitle(), response.getData().getTitle());
        verify(tourRepository, times(1)).findByIdAndIsDeletedFalse(1L);
        verify(tourScheduleRepository, times(1)).save(any(TourScheduleEntity.class));
    }

    @Test
    void updateTourSchedule_Success() {
        when(tourScheduleRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockSchedule));
        when(tourScheduleRepository.save(any(TourScheduleEntity.class))).thenReturn(mockSchedule);

        BaseResponse<TourScheduleEntity> response = tourScheduleService.updateTourSchedule(1L, mockDto);

        assertNotNull(response.getData());
        verify(tourScheduleRepository, times(1)).findByIdAndIsDeletedFalse(1L);
        verify(tourScheduleRepository, times(1)).save(any(TourScheduleEntity.class));
    }

    @Test
    void deleteTourSchedule_Success() {
        when(tourScheduleRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockSchedule));
        when(tourScheduleRepository.save(any(TourScheduleEntity.class))).thenReturn(mockSchedule);

        BaseResponse<Boolean> response = tourScheduleService.deleteTourSchedule(1L);

        assertTrue(response.getData());
        assertTrue(mockSchedule.getIsDeleted());
        verify(tourScheduleRepository, times(1)).save(mockSchedule);
    }

    @Test
    void validateLong_Fail() {
        assertThrows(InvalidInputException.class, () -> tourScheduleService.getSchedulesByTourId(null));
        assertThrows(InvalidInputException.class, () -> tourScheduleService.getSchedulesByTourId(0L));
    }
}
