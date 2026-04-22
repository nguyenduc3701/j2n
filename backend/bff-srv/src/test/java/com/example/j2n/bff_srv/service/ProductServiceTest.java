package com.example.j2n.bff_srv.service;

import com.example.j2n.bff_srv.controller.request.CategoryRequest;
import com.example.j2n.bff_srv.controller.request.ProductImageRequest;
import com.example.j2n.bff_srv.controller.request.ProductRequest;
import com.example.j2n.bff_srv.controller.request.ProductScheduleRequest;
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
        private ProductService productService;

        private static final String PRODUCT_CATEGORY_DOC = "product-category";
        private static final String PRODUCT_TOUR_DOC = "product";

        // ===================== Category Methods =====================

        @Test
        void getAllCategories_ShouldReturnSuccess() {
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq(PRODUCT_CATEGORY_DOC), eq("getAllCategories"), isNull(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.getAllCategories();

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq(PRODUCT_CATEGORY_DOC), eq("getAllCategories"), isNull(),
                                any(ParameterizedTypeReference.class));
        }

        @Test
        void getCategoryById_ShouldReturnSuccess() {
                Long id = 1L;
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq(PRODUCT_CATEGORY_DOC), eq("getCategoryById"), anyMap(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.getCategoryById(id);

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq(PRODUCT_CATEGORY_DOC), eq("getCategoryById"), eq(Map.of("id", id)),
                                any(ParameterizedTypeReference.class));
        }

        @Test
        void getCategoryBySlug_ShouldReturnSuccess() {
                String slug = "test-slug";
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq(PRODUCT_CATEGORY_DOC), eq("getCategoryBySlug"), anyMap(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.getCategoryBySlug(slug);

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq(PRODUCT_CATEGORY_DOC), eq("getCategoryBySlug"),
                                eq(Map.of("slug", slug)), any(ParameterizedTypeReference.class));
        }

        @Test
        void getCategoryByName_ShouldReturnSuccess() {
                String name = "test-name";
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq(PRODUCT_CATEGORY_DOC), eq("getCategoryByName"), anyMap(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.getCategoryByName(name);

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq(PRODUCT_CATEGORY_DOC), eq("getCategoryByName"),
                                eq(Map.of("name", name)), any(ParameterizedTypeReference.class));
        }

        @Test
        void createCategory_ShouldReturnSuccess() {
                CategoryRequest input = CategoryRequest.builder().name("Test").slug("test").build();
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq(PRODUCT_CATEGORY_DOC), eq("createCategory"), anyMap(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.createCategory(input);

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq(PRODUCT_CATEGORY_DOC), eq("createCategory"),
                                eq(Map.of("input", input)), any(ParameterizedTypeReference.class));
        }

        @Test
        void updateCategory_ShouldReturnSuccess() {
                Long id = 1L;
                CategoryRequest input = CategoryRequest.builder().name("Test Updated").slug("test-updated").build();
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq(PRODUCT_CATEGORY_DOC), eq("updateCategory"), anyMap(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.updateCategory(id, input);

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq(PRODUCT_CATEGORY_DOC), eq("updateCategory"),
                                eq(Map.of("id", id, "input", input)), any(ParameterizedTypeReference.class));
        }

        @Test
        void deleteCategory_ShouldReturnSuccess() {
                Long id = 1L;
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq(PRODUCT_CATEGORY_DOC), eq("deleteCategory"), anyMap(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.deleteCategory(id);

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq(PRODUCT_CATEGORY_DOC), eq("deleteCategory"), eq(Map.of("id", id)),
                                any(ParameterizedTypeReference.class));
        }

        // ===================== Product Methods =====================

        @Test
        void getAllProducts_ShouldReturnSuccess() {
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq(PRODUCT_TOUR_DOC), eq("getAllProducts"), isNull(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.getAllProducts();

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq(PRODUCT_TOUR_DOC), eq("getAllProducts"), isNull(),
                                any(ParameterizedTypeReference.class));
        }

        @Test
        void getProductById_ShouldReturnSuccess() {
                Long id = 1L;
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq(PRODUCT_TOUR_DOC), eq("getProductById"), anyMap(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.getProductById(id);

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq(PRODUCT_TOUR_DOC), eq("getProductById"), eq(Map.of("id", id)),
                                any(ParameterizedTypeReference.class));
        }

        @Test
        void getProductsByCategory_ShouldReturnSuccess() {
                Long categoryId = 2L;
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq(PRODUCT_TOUR_DOC), eq("getProductsByCategory"), anyMap(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.getProductsByCategory(categoryId);

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq(PRODUCT_TOUR_DOC), eq("getProductsByCategory"),
                                eq(Map.of("categoryId", categoryId)), any(ParameterizedTypeReference.class));
        }

        @Test
        void createProduct_ShouldReturnSuccess() {
                ProductRequest input = ProductRequest.builder().title("Product").categoryId(1L).build();
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq(PRODUCT_TOUR_DOC), eq("createProduct"), anyMap(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.createProduct(input);

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq(PRODUCT_TOUR_DOC), eq("createProduct"), eq(Map.of("input", input)),
                                any(ParameterizedTypeReference.class));
        }

        @Test
        void updateProduct_ShouldReturnSuccess() {
                Long id = 1L;
                ProductRequest input = ProductRequest.builder().title("Product Updated").build();
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq(PRODUCT_TOUR_DOC), eq("updateProduct"), anyMap(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.updateProduct(id, input);

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq(PRODUCT_TOUR_DOC), eq("updateProduct"),
                                eq(Map.of("id", id, "input", input)), any(ParameterizedTypeReference.class));
        }

        @Test
        void deleteProduct_ShouldReturnSuccess() {
                Long id = 1L;
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq(PRODUCT_TOUR_DOC), eq("deleteProduct"), anyMap(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.deleteProduct(id);

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq(PRODUCT_TOUR_DOC), eq("deleteProduct"), eq(Map.of("id", id)),
                                any(ParameterizedTypeReference.class));
        }

        // ===================== Product Image Methods =====================

        @Test
        void getImagesByProductId_ShouldReturnSuccess() {
                Long productId = 1L;
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq("product-image"), eq("getImagesByProductId"), anyMap(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.getImagesByProductId(productId);

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq("product-image"), eq("getImagesByProductId"),
                                eq(Map.of("productId", productId)), any(ParameterizedTypeReference.class));
        }

        @Test
        void addImageToProduct_ShouldReturnSuccess() {
                ProductImageRequest input = ProductImageRequest.builder().productId(1L).imageUrl("url").build();
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq("product-image"), eq("addImageToProduct"), anyMap(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.addImageToProduct(input);

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq("product-image"), eq("addImageToProduct"), eq(Map.of("input", input)),
                                any(ParameterizedTypeReference.class));
        }

        @Test
        void deleteProductImage_ShouldReturnSuccess() {
                Long id = 1L;
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq("product-image"), eq("deleteProductImage"), anyMap(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.deleteProductImage(id);

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq("product-image"), eq("deleteProductImage"), eq(Map.of("id", id)),
                                any(ParameterizedTypeReference.class));
        }

        @Test
        void setPrimaryImage_ShouldReturnSuccess() {
                Long id = 1L;
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq("product-image"), eq("setPrimaryImage"), anyMap(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.setPrimaryImage(id);

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq("product-image"), eq("setPrimaryImage"), eq(Map.of("id", id)),
                                any(ParameterizedTypeReference.class));
        }

        // ===================== Product Schedule Methods =====================

        @Test
        void getSchedulesByProductId_ShouldReturnSuccess() {
                Long productId = 1L;
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq("product-schedule"), eq("getSchedulesByProductId"), anyMap(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.getSchedulesByProductId(productId);

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq("product-schedule"), eq("getSchedulesByProductId"),
                                eq(Map.of("productId", productId)), any(ParameterizedTypeReference.class));
        }

        @Test
        void addScheduleToProduct_ShouldReturnSuccess() {
                ProductScheduleRequest input = ProductScheduleRequest.builder().productId(1L).dayNumber(1)
                                .title("Day 1").build();
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq("product-schedule"), eq("addScheduleToProduct"), anyMap(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.addScheduleToProduct(input);

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq("product-schedule"), eq("addScheduleToProduct"),
                                eq(Map.of("input", input)), any(ParameterizedTypeReference.class));
        }

        @Test
        void updateProductSchedule_ShouldReturnSuccess() {
                Long id = 1L;
                ProductScheduleRequest input = ProductScheduleRequest.builder().title("Day 1 Updated").build();
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq("product-schedule"), eq("updateProductSchedule"), anyMap(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.updateProductSchedule(id, input);

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq("product-schedule"), eq("updateProductSchedule"),
                                eq(Map.of("id", id, "input", input)), any(ParameterizedTypeReference.class));
        }

        @Test
        void deleteProductSchedule_ShouldReturnSuccess() {
                Long id = 1L;
                Object expectedResponse = new Object();
                when(graphQLFactory.execute(eq("product-schedule"), eq("deleteProductSchedule"), anyMap(),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(Mono.just(expectedResponse));

                Mono<Object> result = productService.deleteProductSchedule(id);

                StepVerifier.create(result)
                                .expectNext(expectedResponse)
                                .verifyComplete();

                verify(graphQLFactory).execute(eq("product-schedule"), eq("deleteProductSchedule"),
                                eq(Map.of("id", id)), any(ParameterizedTypeReference.class));
        }
}
