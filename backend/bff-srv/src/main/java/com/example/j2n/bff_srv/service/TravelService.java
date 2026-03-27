package com.example.j2n.bff_srv.service;

import com.example.j2n.bff_srv.utils.GraphQLFactory;
import com.example.j2n.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class TravelService {

  private final GraphQLFactory graphQLFactory;

  public Mono<BaseResponse<String>> getHello() {
    return graphQLFactory.execute("travel", "hello", null,
        new ParameterizedTypeReference<BaseResponse<String>>() {
        });
  }
}
