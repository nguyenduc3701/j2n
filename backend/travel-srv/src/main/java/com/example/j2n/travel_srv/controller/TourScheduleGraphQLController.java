package com.example.j2n.travel_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.travel_srv.dto.TourScheduleDto;
import com.example.j2n.travel_srv.repository.entity.TourScheduleEntity;
import com.example.j2n.travel_srv.service.TourScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TourScheduleGraphQLController {

    private final TourScheduleService tourScheduleService;

    // --- Queries ---

    @QueryMapping(name = "getSchedulesByTourId")
    public BaseResponse<List<TourScheduleEntity>> getSchedulesByTourId(@Argument Long tourId) {
        return tourScheduleService.getSchedulesByTourId(tourId);
    }

    // --- Mutations ---

    @MutationMapping(name = "addScheduleToTour")
    public BaseResponse<TourScheduleEntity> addScheduleToTour(@Argument @Valid TourScheduleDto input) {
        return tourScheduleService.addScheduleToTour(input);
    }

    @MutationMapping(name = "updateTourSchedule")
    public BaseResponse<TourScheduleEntity> updateTourSchedule(@Argument Long id, @Argument @Valid TourScheduleDto input) {
        return tourScheduleService.updateTourSchedule(id, input);
    }

    @MutationMapping(name = "deleteTourSchedule")
    public BaseResponse<Boolean> deleteTourSchedule(@Argument Long id) {
        return tourScheduleService.deleteTourSchedule(id);
    }
}
