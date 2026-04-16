package com.example.j2n.travel_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.travel_srv.dto.TourImageDto;
import com.example.j2n.travel_srv.repository.entity.TourImageEntity;
import com.example.j2n.travel_srv.service.TourImageService;
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
public class TourImageGraphQLController {

    private final TourImageService tourImageService;

    // --- Queries ---

    @QueryMapping(name = "getImagesByTourId")
    public BaseResponse<List<TourImageEntity>> getImagesByTourId(@Argument Long tourId) {
        return tourImageService.getImagesByTourId(tourId);
    }

    // --- Mutations ---

    @MutationMapping(name = "addImageToTour")
    public BaseResponse<TourImageEntity> addImageToTour(@Argument @Valid TourImageDto input) {
        return tourImageService.addImageToTour(input);
    }

    @MutationMapping(name = "deleteTourImage")
    public BaseResponse<Boolean> deleteTourImage(@Argument Long id) {
        return tourImageService.deleteTourImage(id);
    }

    @MutationMapping(name = "setPrimaryImage")
    public BaseResponse<TourImageEntity> setPrimaryImage(@Argument Long id) {
        return tourImageService.setPrimaryImage(id);
    }
}
