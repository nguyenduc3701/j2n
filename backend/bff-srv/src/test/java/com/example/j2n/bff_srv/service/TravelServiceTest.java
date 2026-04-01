package com.example.j2n.bff_srv.service;

import com.example.j2n.bff_srv.utils.GraphQLFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TravelServiceTest {

    @Mock
    private GraphQLFactory graphQLFactory;

    @InjectMocks
    private TravelService travelService;

    private static final String TRAVEL_CATEGORY_DOC = "travel-category";

    @Test
    void getAllCategories_ShouldReturnSuccess() {
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_CATEGORY_DOC), eq("getAllCategories"), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.getAllCategories();

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_CATEGORY_DOC), eq("getAllCategories"), isNull(), any(ParameterizedTypeReference.class));
    }

    @Test
    void getCategoryById_ShouldReturnSuccess() {
        Long id = 1L;
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_CATEGORY_DOC), eq("getCategoryById"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.getCategoryById(id);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_CATEGORY_DOC), eq("getCategoryById"), eq(Map.of("id", id)), any(ParameterizedTypeReference.class));
    }

    @Test
    void getCategoryBySlug_ShouldReturnSuccess() {
        String slug = "test-slug";
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_CATEGORY_DOC), eq("getCategoryBySlug"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.getCategoryBySlug(slug);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_CATEGORY_DOC), eq("getCategoryBySlug"), eq(Map.of("slug", slug)), any(ParameterizedTypeReference.class));
    }

    @Test
    void getCategoryByName_ShouldReturnSuccess() {
        String name = "test-name";
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_CATEGORY_DOC), eq("getCategoryByName"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.getCategoryByName(name);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_CATEGORY_DOC), eq("getCategoryByName"), eq(Map.of("name", name)), any(ParameterizedTypeReference.class));
    }

    @Test
    void createCategory_ShouldReturnSuccess() {
        Object input = new Object();
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_CATEGORY_DOC), eq("createCategory"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.createCategory(input);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_CATEGORY_DOC), eq("createCategory"), eq(Map.of("input", input)), any(ParameterizedTypeReference.class));
    }

    @Test
    void updateCategory_ShouldReturnSuccess() {
        Long id = 1L;
        Object input = new Object();
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_CATEGORY_DOC), eq("updateCategory"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.updateCategory(id, input);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_CATEGORY_DOC), eq("updateCategory"), eq(Map.of("id", id, "input", input)), any(ParameterizedTypeReference.class));
    }

    @Test
    void deleteCategory_ShouldReturnSuccess() {
        Long id = 1L;
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_CATEGORY_DOC), eq("deleteCategory"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.deleteCategory(id);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_CATEGORY_DOC), eq("deleteCategory"), eq(Map.of("id", id)), any(ParameterizedTypeReference.class));
    }
}
