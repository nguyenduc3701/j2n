package com.example.j2n.bff_srv.service;


import com.example.j2n.bff_srv.controller.request.CategoryRequest;
import com.example.j2n.bff_srv.controller.request.TourImageRequest;
import com.example.j2n.bff_srv.controller.request.TourRequest;
import com.example.j2n.bff_srv.controller.request.TourScheduleRequest;
import com.example.j2n.bff_srv.utils.GraphQLFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TravelService {
  private final static String TRAVEL_CATEGORY_DOC = "travel-category";
  private final static String TRAVEL_TOUR_DOC = "travel-tour";
  private final static String TRAVEL_IMAGE_DOC = "travel-image";
  private final static String TRAVEL_SCHEDULE_DOC = "travel-schedule";
  private final static String ID = "id";
  private final static String SLUG = "slug";
  private final static String NAME = "name";
  private final static String INPUT = "input";
  private final static String CATEGORY_ID = "categoryId";
  private final static String TOUR_ID = "tourId";

  private final GraphQLFactory graphQLFactory;

  public Mono<Object> getAllCategories() {
    return graphQLFactory.execute(TRAVEL_CATEGORY_DOC, "getAllCategories", null,
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> getCategoryById(Long categoryId) {
    return graphQLFactory.execute(TRAVEL_CATEGORY_DOC, "getCategoryById", Map.of(ID, categoryId),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> getCategoryBySlug(String slug) {
    return graphQLFactory.execute(TRAVEL_CATEGORY_DOC, "getCategoryBySlug", Map.of(SLUG, slug),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> getCategoryByName(String name) {
    return graphQLFactory.execute(TRAVEL_CATEGORY_DOC, "getCategoryByName", Map.of(NAME, name),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> createCategory(CategoryRequest input) {
    return graphQLFactory.execute(TRAVEL_CATEGORY_DOC, "createCategory", Map.of(INPUT, input),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> updateCategory(Long categoryId, CategoryRequest input) {
    return graphQLFactory.execute(TRAVEL_CATEGORY_DOC, "updateCategory", Map.of(ID, categoryId, INPUT, input),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> deleteCategory(Long categoryId) {
    return graphQLFactory.execute(TRAVEL_CATEGORY_DOC, "deleteCategory", Map.of(ID, categoryId),
        new ParameterizedTypeReference<Object>() {
        });
  }

  // --- Tour methods ---

  public Mono<Object> getAllTours() {
    return graphQLFactory.execute(TRAVEL_TOUR_DOC, "getAllTours", null,
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> getTourById(Long tourId) {
    return graphQLFactory.execute(TRAVEL_TOUR_DOC, "getTourById", Map.of(ID, tourId),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> getToursByCategory(Long categoryId) {
    return graphQLFactory.execute(TRAVEL_TOUR_DOC, "getToursByCategory", Map.of(CATEGORY_ID, categoryId),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> createTour(TourRequest input) {
    return graphQLFactory.execute(TRAVEL_TOUR_DOC, "createTour", Map.of(INPUT, input),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> updateTour(Long tourId, TourRequest input) {
    return graphQLFactory.execute(TRAVEL_TOUR_DOC, "updateTour", Map.of(ID, tourId, INPUT, input),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> deleteTour(Long tourId) {
    return graphQLFactory.execute(TRAVEL_TOUR_DOC, "deleteTour", Map.of(ID, tourId),
        new ParameterizedTypeReference<Object>() {
        });
  }

  // --- Tour Image methods ---

  public Mono<Object> getImagesByTourId(Long tourId) {
    return graphQLFactory.execute(TRAVEL_IMAGE_DOC, "getImagesByTourId", Map.of(TOUR_ID, tourId),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> addImageToTour(TourImageRequest input) {
    return graphQLFactory.execute(TRAVEL_IMAGE_DOC, "addImageToTour", Map.of(INPUT, input),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> deleteTourImage(Long imageId) {
    return graphQLFactory.execute(TRAVEL_IMAGE_DOC, "deleteTourImage", Map.of(ID, imageId),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> setPrimaryImage(Long imageId) {
    return graphQLFactory.execute(TRAVEL_IMAGE_DOC, "setPrimaryImage", Map.of(ID, imageId),
        new ParameterizedTypeReference<Object>() {
        });
  }

  // --- Tour Schedule methods ---

  public Mono<Object> getSchedulesByTourId(Long tourId) {
    return graphQLFactory.execute(TRAVEL_SCHEDULE_DOC, "getSchedulesByTourId", Map.of(TOUR_ID, tourId),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> addScheduleToTour(TourScheduleRequest input) {
    return graphQLFactory.execute(TRAVEL_SCHEDULE_DOC, "addScheduleToTour", Map.of(INPUT, input),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> updateTourSchedule(Long scheduleId, TourScheduleRequest input) {
    return graphQLFactory.execute(TRAVEL_SCHEDULE_DOC, "updateTourSchedule", Map.of(ID, scheduleId, INPUT, input),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> deleteTourSchedule(Long scheduleId) {
    return graphQLFactory.execute(TRAVEL_SCHEDULE_DOC, "deleteTourSchedule", Map.of(ID, scheduleId),
        new ParameterizedTypeReference<Object>() {
        });
  }
}
