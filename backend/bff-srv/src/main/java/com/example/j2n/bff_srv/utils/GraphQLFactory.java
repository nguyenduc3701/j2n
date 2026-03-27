package com.example.j2n.bff_srv.utils;

import com.example.j2n.dto.BaseResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GraphQLFactory {
    private final HttpGraphQlClient client;

    @SuppressWarnings("unchecked")
    public <T> Mono<T> execute(String docName, String path, Map<String, Object> vars,
            ParameterizedTypeReference<T> type) {
        
        // Lấy RequestContext ở thread chính trước khi chuyển sang thread bất đồng bộ của WebClient
        org.springframework.web.context.request.RequestAttributes attrs = 
                org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();

        HttpGraphQlClient.RequestSpec request = client.documentName(docName);
        if (vars != null) {
            request = request.variables(vars);
        }
        return request.retrieve(path)
                .toEntity(type)
                .onErrorResume(ex -> {
                    log.error("GraphQL execution error - doc: {}, path: {}", docName, path, ex);
                    try {
                        // Assuming T is BaseResponse or compatible
                        BaseResponse<?> errorResponse = new BaseResponse<>();
                        errorResponse.setCode("500");
                        errorResponse.setMessage("BFF Error: " + ex.getMessage());
                        return Mono.just((T) errorResponse);
                    } catch (Exception e) {
                        log.error("Failed to create error response", e);
                        return Mono.error(ex);
                    }
                })
                .contextWrite(ctx -> attrs != null ? ctx.put(org.springframework.web.context.request.RequestAttributes.class, attrs) : ctx);
    }
}
