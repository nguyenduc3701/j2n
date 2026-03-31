package com.example.j2n.bff_srv.service;

import com.example.j2n.bff_srv.dto.CategoryDto;
import com.example.j2n.bff_srv.dto.CategoryResponse;
import com.example.j2n.bff_srv.utils.GraphQLFactory;
import com.example.j2n.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TravelService {
  private final static String DOC_NAME = "travel";
  private final static String ID = "id";
  private final static String SLUG = "slug";
  private final static String NAME = "name";
  private final static String INPUT = "input";

  private final GraphQLFactory graphQLFactory;

  public Mono<BaseResponse<String>> getHello() {
    return graphQLFactory.execute(DOC_NAME, "hello", null,
        new ParameterizedTypeReference<BaseResponse<String>>() {
        });
  }

  public Mono<BaseResponse<List<CategoryResponse>>> getAllCategories() {
    return graphQLFactory.execute(DOC_NAME, "getAllCategories", null,
        new ParameterizedTypeReference<BaseResponse<List<CategoryResponse>>>() {
        });
  }

  public Mono<BaseResponse<CategoryResponse>> getCategoryById(Long id) {
    return graphQLFactory.execute(DOC_NAME, "getCategoryById", Map.of(ID, id),
        new ParameterizedTypeReference<BaseResponse<CategoryResponse>>() {
        });
  }

  public Mono<BaseResponse<CategoryResponse>> getCategoryBySlug(String slug) {
    return graphQLFactory.execute(DOC_NAME, "getCategoryBySlug", Map.of(SLUG, slug),
        new ParameterizedTypeReference<BaseResponse<CategoryResponse>>() {
        });
  }

  public Mono<BaseResponse<CategoryResponse>> getCategoryByName(String name) {
    return graphQLFactory.execute(DOC_NAME, "getCategoryByName", Map.of(NAME, name),
        new ParameterizedTypeReference<BaseResponse<CategoryResponse>>() {
        });
  }

  public Mono<BaseResponse<CategoryResponse>> createCategory(CategoryDto input) {
    return graphQLFactory.execute(DOC_NAME, "createCategory", Map.of(INPUT, input),
        new ParameterizedTypeReference<BaseResponse<CategoryResponse>>() {
        });
  }

  public Mono<BaseResponse<CategoryResponse>> updateCategory(Long id, CategoryDto input) {
    return graphQLFactory.execute(DOC_NAME, "updateCategory", Map.of(ID, id, INPUT, input),
        new ParameterizedTypeReference<BaseResponse<CategoryResponse>>() {
        });
  }

  public Mono<BaseResponse<Boolean>> deleteCategory(Long id) {
    return graphQLFactory.execute(DOC_NAME, "deleteCategory", Map.of(ID, id),
        new ParameterizedTypeReference<BaseResponse<Boolean>>() {
        });
  }
}
