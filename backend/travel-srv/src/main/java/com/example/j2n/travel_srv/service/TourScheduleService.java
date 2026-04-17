package com.example.j2n.travel_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.utils.ResponseFactory;
import com.example.j2n.utils.ValidationUtils;
import com.example.j2n.travel_srv.constant.MessageEnum;
import com.example.j2n.travel_srv.dto.TourScheduleDto;
import com.example.j2n.travel_srv.repository.TourScheduleRepository;
import com.example.j2n.travel_srv.repository.entity.TourEntity;
import com.example.j2n.travel_srv.repository.entity.TourScheduleEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TourScheduleService {

    private final TourScheduleRepository tourScheduleRepository;
    private final TourService tourService;

    @LogAround(message = "Get schedules by tour id")
    public BaseResponse<List<TourScheduleEntity>> getSchedulesByTourId(Long tourId) {
        ValidationUtils.validateLong(tourId);
        return ResponseFactory.success(tourScheduleRepository.findAllByTourIdAndIsDeletedFalseOrderByDayNumberAsc(tourId));
    }

    @Transactional
    @LogAround(message = "Add schedule to tour")
    public BaseResponse<TourScheduleEntity> addScheduleToTour(TourScheduleDto input) {
        TourEntity tour = tourService.getTourByIdOrThrow(input.getTourId());

        TourScheduleEntity entity = TourScheduleEntity.builder()
                .tour(tour)
                .dayNumber(input.getDayNumber())
                .title(input.getTitle())
                .content(input.getContent())
                .isDeleted(false)
                .build();

        return ResponseFactory.success(tourScheduleRepository.save(entity));
    }

    @Transactional
    @LogAround(message = "Update tour schedule")
    public BaseResponse<TourScheduleEntity> updateTourSchedule(Long id, TourScheduleDto input) {
        TourScheduleEntity entity = getTourScheduleByIdOrThrow(id);

        entity.setDayNumber(input.getDayNumber());
        entity.setTitle(input.getTitle());
        entity.setContent(input.getContent());

        return ResponseFactory.success(tourScheduleRepository.save(entity));
    }

    @Transactional
    @LogAround(message = "Delete tour schedule")
    public BaseResponse<Boolean> deleteTourSchedule(Long id) {
        TourScheduleEntity entity = getTourScheduleByIdOrThrow(id);
        entity.setIsDeleted(true);
        tourScheduleRepository.save(entity);
        return ResponseFactory.success(true);
    }

    // --- Private helpers ---

    private TourScheduleEntity getTourScheduleByIdOrThrow(Long id) {
        ValidationUtils.validateLong(id);
        return tourScheduleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.TOUR_SCHEDULE_NOT_FOUND));
    }
}
