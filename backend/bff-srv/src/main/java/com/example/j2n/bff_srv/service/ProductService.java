package com.example.j2n.bff_srv.service;

import com.example.j2n.bff_srv.controller.request.CategoryRequest;
import com.example.j2n.bff_srv.controller.request.ProductImageRequest;
import com.example.j2n.bff_srv.controller.request.ProductRequest;
import com.example.j2n.bff_srv.controller.request.ProductScheduleRequest;
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
public class ProductService {
  private final static String PRODUCT_CATEGORY_DOC = "product-category";
  private final static String PRODUCT_DOC = "product";
  private final static String PRODUCT_IMAGE_DOC = "product-image";
  private final static String PRODUCT_SCHEDULE_DOC = "product-schedule";
  private final static String ID = "id";
  private final static String SLUG = "slug";
  private final static String NAME = "name";
  private final static String INPUT = "input";
  private final static String CATEGORY_ID = "categoryId";
  private final static String PRODUCT_ID = "productId";

  private final GraphQLFactory graphQLFactory;

  public Mono<Object> getAllCategories() {
    return graphQLFactory.execute(PRODUCT_CATEGORY_DOC, "getAllCategories", null,
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> getCategoryById(Long categoryId) {
    return graphQLFactory.execute(PRODUCT_CATEGORY_DOC, "getCategoryById", Map.of(ID, categoryId),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> getCategoryBySlug(String slug) {
    return graphQLFactory.execute(PRODUCT_CATEGORY_DOC, "getCategoryBySlug", Map.of(SLUG, slug),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> getCategoryByName(String name) {
    return graphQLFactory.execute(PRODUCT_CATEGORY_DOC, "getCategoryByName", Map.of(NAME, name),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> createCategory(CategoryRequest input) {
    return graphQLFactory.execute(PRODUCT_CATEGORY_DOC, "createCategory", Map.of(INPUT, input),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> updateCategory(Long categoryId, CategoryRequest input) {
    return graphQLFactory.execute(PRODUCT_CATEGORY_DOC, "updateCategory", Map.of(ID, categoryId, INPUT, input),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> deleteCategory(Long categoryId) {
    return graphQLFactory.execute(PRODUCT_CATEGORY_DOC, "deleteCategory", Map.of(ID, categoryId),
        new ParameterizedTypeReference<Object>() {
        });
  }

  // --- Product methods ---

  public Mono<Object> getAllProducts() {
    return graphQLFactory.execute(PRODUCT_DOC, "getAllProducts", null,
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> getProductById(Long productId) {
    return graphQLFactory.execute(PRODUCT_DOC, "getProductById", Map.of(ID, productId),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> getProductsByCategory(Long categoryId) {
    return graphQLFactory.execute(PRODUCT_DOC, "getProductsByCategory", Map.of(CATEGORY_ID, categoryId),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> getProductsByType(String type) {
    return graphQLFactory.execute(PRODUCT_DOC, "getProductsByType", Map.of("type", type),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> getCategoriesByType(String type) {
    return graphQLFactory.execute(PRODUCT_CATEGORY_DOC, "getCategoriesByType", Map.of("type", type),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> createProduct(ProductRequest input) {
    return graphQLFactory.execute(PRODUCT_DOC, "createProduct", Map.of(INPUT, input),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> updateProduct(Long productId, ProductRequest input) {
    return graphQLFactory.execute(PRODUCT_DOC, "updateProduct", Map.of(ID, productId, INPUT, input),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> deleteProduct(Long productId) {
    return graphQLFactory.execute(PRODUCT_DOC, "deleteProduct", Map.of(ID, productId),
        new ParameterizedTypeReference<Object>() {
        });
  }

  // --- Product Image methods ---

  public Mono<Object> getImagesByProductId(Long productId) {
    return graphQLFactory.execute(PRODUCT_IMAGE_DOC, "getImagesByProductId", Map.of(PRODUCT_ID, productId),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> addImageToProduct(ProductImageRequest input) {
    return graphQLFactory.execute(PRODUCT_IMAGE_DOC, "addImageToProduct", Map.of(INPUT, input),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> deleteProductImage(Long imageId) {
    return graphQLFactory.execute(PRODUCT_IMAGE_DOC, "deleteProductImage", Map.of(ID, imageId),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> setPrimaryImage(Long imageId) {
    return graphQLFactory.execute(PRODUCT_IMAGE_DOC, "setPrimaryImage", Map.of(ID, imageId),
        new ParameterizedTypeReference<Object>() {
        });
  }

  // --- Product Schedule methods ---

  public Mono<Object> getSchedulesByProductId(Long productId) {
    return graphQLFactory.execute(PRODUCT_SCHEDULE_DOC, "getSchedulesByProductId", Map.of(PRODUCT_ID, productId),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> addScheduleToProduct(ProductScheduleRequest input) {
    return graphQLFactory.execute(PRODUCT_SCHEDULE_DOC, "addScheduleToProduct", Map.of(INPUT, input),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> updateProductSchedule(Long scheduleId, ProductScheduleRequest input) {
    return graphQLFactory.execute(PRODUCT_SCHEDULE_DOC, "updateProductSchedule", Map.of(ID, scheduleId, INPUT, input),
        new ParameterizedTypeReference<Object>() {
        });
  }

  public Mono<Object> deleteProductSchedule(Long scheduleId) {
    return graphQLFactory.execute(PRODUCT_SCHEDULE_DOC, "deleteProductSchedule", Map.of(ID, scheduleId),
        new ParameterizedTypeReference<Object>() {
        });
  }
}
