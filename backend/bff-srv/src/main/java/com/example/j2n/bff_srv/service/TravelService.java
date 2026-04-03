package com.example.j2n.bff_srv.service;


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
  private final static String ID = "id";
  private final static String SLUG = "slug";
  private final static String NAME = "name";
  private final static String INPUT = "input";
  private final static String CATEGORY_ID = "categoryId";

  private final GraphQLFactory graphQLFactory;

  public Mono<Object> getAllCategories() {
    return graphQLFactory.execute(TRAVEL_CATEGORY_DOC, "getAllCategories", null,
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> getCategoryById(Long id) {
    return graphQLFactory.execute(TRAVEL_CATEGORY_DOC, "getCategoryById", Map.of(ID, id),
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

  public Mono<Object> createCategory(Object input) {
    return graphQLFactory.execute(TRAVEL_CATEGORY_DOC, "createCategory", Map.of(INPUT, input),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> updateCategory(Long id, Object input) {
    return graphQLFactory.execute(TRAVEL_CATEGORY_DOC, "updateCategory", Map.of(ID, id, INPUT, input),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> deleteCategory(Long id) {
    return graphQLFactory.execute(TRAVEL_CATEGORY_DOC, "deleteCategory", Map.of(ID, id),
        new ParameterizedTypeReference<Object>() {
        });
  }

  // --- Tour methods ---

  public Mono<Object> getAllTours() {
    return graphQLFactory.execute(TRAVEL_TOUR_DOC, "getAllTours", null,
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> getTourById(Long id) {
    return graphQLFactory.execute(TRAVEL_TOUR_DOC, "getTourById", Map.of(ID, id),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> getToursByCategory(Long categoryId) {
    return graphQLFactory.execute(TRAVEL_TOUR_DOC, "getToursByCategory", Map.of(CATEGORY_ID, categoryId),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> createTour(Object input) {
    return graphQLFactory.execute(TRAVEL_TOUR_DOC, "createTour", Map.of(INPUT, input),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> updateTour(Long id, Object input) {
    return graphQLFactory.execute(TRAVEL_TOUR_DOC, "updateTour", Map.of(ID, id, INPUT, input),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> deleteTour(Long id) {
    return graphQLFactory.execute(TRAVEL_TOUR_DOC, "deleteTour", Map.of(ID, id),
        new ParameterizedTypeReference<Object>() {
        });
  }
}
