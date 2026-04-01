package com.example.j2n.travel_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.travel_srv.dto.TourDto;
import com.example.j2n.travel_srv.repository.entity.TourEntity;
import com.example.j2n.travel_srv.service.TourService;
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
public class TourGraphQLController {

    private final TourService tourService;

    // --- Queries ---

    @QueryMapping(name = "getAllTours")
    public BaseResponse<List<TourEntity>> getAllTours() {
        return tourService.getAllTours();
    }

    @QueryMapping(name = "getTourById")
    public BaseResponse<TourEntity> getTourById(@Argument Long id) {
        return tourService.getTourById(id);
    }

    @QueryMapping(name = "getToursByCategory")
    public BaseResponse<List<TourEntity>> getToursByCategory(@Argument Long categoryId) {
        return tourService.getToursByCategory(categoryId);
    }

    // --- Mutations ---

    @MutationMapping(name = "createTour")
    public BaseResponse<TourEntity> createTour(@Argument @Valid TourDto input) {
        return tourService.createTour(input);
    }

    @MutationMapping(name = "updateTour")
    public BaseResponse<TourEntity> updateTour(@Argument Long id, @Argument @Valid TourDto input) {
        return tourService.updateTour(id, input);
    }

    @MutationMapping(name = "deleteTour")
    public BaseResponse<Boolean> deleteTour(@Argument Long id) {
        return tourService.deleteTour(id);
    }
}
