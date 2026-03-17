package com.example.j2n.auth_srv.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import com.example.j2n.auth_srv.swagger.J2NApiRole;
import com.example.j2n.constants.CommonConst;
import com.example.j2n.impl.BaseMessage;
import com.example.j2n.auth_srv.swagger.J2NApiExample;
import com.example.j2n.auth_srv.swagger.J2NApiResponse;
import com.example.j2n.auth_srv.swagger.J2NApiResponses;
import com.example.j2n.auth_srv.constant.MessageEnum;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Configuration
public class OpenApiConfig {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new Info()
                        .title("Auth Service API")
                        .version("v1")
                        .description("Authentication & Authorization APIs"));
    }

    @Bean
    public OperationCustomizer customize() {
        return (operation, handlerMethod) -> {
            applyRolePermissions(operation, handlerMethod);
            applyRequestBodyExample(operation, handlerMethod);
            applyCustomResponses(operation, handlerMethod);
            applyCustomParameters(operation, handlerMethod);
            return operation;
        };
    }

    private void applyRolePermissions(Operation operation,
            HandlerMethod handlerMethod) {
        J2NApiRole roleAnnotation = handlerMethod.getMethodAnnotation(J2NApiRole.class);
        if (roleAnnotation == null) {
            roleAnnotation = handlerMethod.getBeanType().getAnnotation(J2NApiRole.class);
        }

        if (roleAnnotation != null) {
            String[] rolesArray = roleAnnotation.roles().length > 0 ? roleAnnotation.roles() : roleAnnotation.value();
            String roles = Arrays.stream(rolesArray).collect(Collectors.joining(", "));

            String description = operation.getDescription() != null ? operation.getDescription() : "";
            operation.setDescription(
                    String.format("%s<br><br><br>Required Permissions: [ <b>%s</b> ]", description, roles));
        }
    }

    private void applyRequestBodyExample(Operation operation,
            HandlerMethod handlerMethod) {
        J2NApiExample exampleAnnotation = handlerMethod.getMethodAnnotation(J2NApiExample.class);
        if (exampleAnnotation != null && operation.getRequestBody() != null) {
            Content content = operation.getRequestBody().getContent();
            if (content != null) {
                BaseMessage baseMessage = getBaseMessage(exampleAnnotation);
                String code = baseMessage.getCode();
                String msg = baseMessage.getMessage();
                if (exampleAnnotation.statusMessageArgs().length > 0) {
                    msg = String.format(msg, (Object[]) exampleAnnotation.statusMessageArgs());
                }
                final String finalMsg = msg;

                // Try to resolve data from return type
                Object dataExample = resolveDataExample(handlerMethod);

                content.forEach((mediaTypeKey, mediaType) -> {
                    Example example = createSwaggerExample(exampleAnnotation, code, finalMsg, dataExample);
                    mediaType.addExamples("Custom Example", example);
                });
            }
        }
    }

    private void applyCustomResponses(Operation operation,
            HandlerMethod handlerMethod) {
        J2NApiResponses responsesAnnotation = handlerMethod.getMethodAnnotation(J2NApiResponses.class);
        if (responsesAnnotation == null) {
            return;
        }

        ApiResponses apiResponses = operation.getResponses();
        if (apiResponses == null) {
            apiResponses = new ApiResponses();
            operation.setResponses(apiResponses);
        }

        for (J2NApiResponse item : responsesAnnotation.value()) {
            ApiResponse apiResponse = new ApiResponse();
            StringBuilder descBuilder = new StringBuilder();
            if (!item.description().isEmpty()) {
                descBuilder.append(item.description()).append("<br>");
            }

            Content apiContent = new Content();
            MediaType mediaType = new MediaType();

            for (J2NApiExample example : item.examples()) {
                BaseMessage baseMessage = getBaseMessage(example);
                String code = baseMessage.getCode();
                String msg = baseMessage.getMessage();
                if (example.statusMessageArgs().length > 0) {
                    msg = String.format(msg, (Object[]) example.statusMessageArgs());
                }

                descBuilder.append("- <b>").append(code).append("</b>: ").append(msg).append("<br>");

                // Only resolve data from return type for success responses
                Object dataExample = (item.httpCode() == 200 || item.httpCode() == 201)
                        ? resolveDataExample(handlerMethod)
                        : null;

                Example swaggerExample = createSwaggerExample(example, code, msg, dataExample);
                String exampleKey = generateExampleKey(example);
                mediaType.addExamples(exampleKey, swaggerExample);
            }

            apiResponse.setDescription(descBuilder.toString());
            if (mediaType.getExamples() != null && !mediaType.getExamples().isEmpty()) {
                apiContent.addMediaType("application/json", mediaType);
                apiResponse.setContent(apiContent);
            }
            apiResponses.addApiResponse(String.valueOf(item.httpCode()), apiResponse);
        }
    }

    private Example createSwaggerExample(J2NApiExample annotation, String code, String msg, Object dataExample) {
        Example example = new Example();
        example.setSummary(annotation.summary().isEmpty() ? msg : annotation.summary());

        if (!annotation.value().isEmpty()) {
            example.setValue(parseJsonValue(annotation.value()));
        } else {
            Map<String, Object> exampleMap = new LinkedHashMap<>();
            exampleMap.put("code", code);
            exampleMap.put("data", dataExample);
            exampleMap.put("message", msg);
            example.setValue(exampleMap);
        }
        return example;
    }

    private Object resolveDataExample(HandlerMethod handlerMethod) {
        try {
            Type returnType = handlerMethod.getMethod().getGenericReturnType();
            if (returnType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) returnType;
                // ResponseEntity<BaseResponse<T>> -> get first arg: BaseResponse<T>
                Type baseRespType = pt.getActualTypeArguments()[0];
                if (baseRespType instanceof ParameterizedType) {
                    // BaseResponse<T> -> get first arg: T
                    Type dataType = ((ParameterizedType) baseRespType).getActualTypeArguments()[0];
                    return resolveTypeExample(dataType, new HashSet<>());
                }
            }
        } catch (Exception e) {
            // Fallback to null
        }
        return null;
    }

    private Object resolveTypeExample(Type type, Set<Class<?>> visited) {
        if (type instanceof Class) {
            return createExampleFromClass((Class<?>) type, visited);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            if (pt.getRawType().equals(List.class)) {
                Type itemType = pt.getActualTypeArguments()[0];
                Object itemExample = resolveTypeExample(itemType, visited);
                return itemExample != null ? Collections.singletonList(itemExample) : new ArrayList<>();
            }
        }
        return null;
    }

    private Object createExampleFromClass(Class<?> clazz, Set<Class<?>> visited) {
        if (clazz == null)
            return null;
        if (clazz.equals(String.class))
            return "";
        if (Number.class.isAssignableFrom(clazz) || clazz.isPrimitive())
            return 0;
        if (visited.contains(clazz))
            return null;

        visited.add(clazz);
        try {
            Map<String, Object> map = new HashMap<>();
            for (Field field : clazz.getDeclaredFields()) {
                io.swagger.v3.oas.annotations.media.Schema schema = field
                        .getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class);
                if (schema != null && !schema.example().isEmpty()) {
                    map.put(field.getName(), parseJsonValue(schema.example()));
                } else {
                    Object fieldExample = resolveTypeExample(field.getGenericType(), visited);
                    if (fieldExample != null) {
                        map.put(field.getName(), fieldExample);
                    }
                }
            }
            return map.isEmpty() ? null : map;
        } finally {
            visited.remove(clazz);
        }
    }

    private Object parseJsonValue(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        try {
            return OBJECT_MAPPER.readTree(value);
        } catch (Exception e) {
            return value;
        }
    }

    private String generateExampleKey(J2NApiExample example) {
        BaseMessage baseMessage = getBaseMessage(example);
        String key = baseMessage instanceof Enum ? ((Enum<?>) baseMessage).name() : baseMessage.getCode();
        if (example.statusMessageArgs().length > 0) {
            key += "_" + String.join("_", example.statusMessageArgs());
        }
        return key;
    }

    private BaseMessage getBaseMessage(J2NApiExample annotation) {
        if (annotation.responseStatus() != MessageEnum.NOT_SET) {
            return annotation.responseStatus();
        }
        return annotation.baseResponseStatus();
    }

    private void applyCustomParameters(Operation operation,
            HandlerMethod handlerMethod) {
        if (operation.getParameters() == null) {
            operation.setParameters(new ArrayList<>());
        }

        // 1. Thêm Global Headers (Dựa trên JwtAuthFilter/CurrentUser)
        addGlobalHeader(operation, CommonConst.X_USER_ID, "User ID from Gateway/BFF", "60a7b1b5e4b0a1a2b3c4d5e6");
        addGlobalHeader(operation, CommonConst.X_USER_NAME, "Username from Gateway/BFF", "admin");
        addGlobalHeader(operation, CommonConst.X_ROLE_ID, "Role ID from Gateway/BFF", "ROLE_ADMIN");
        addGlobalHeader(operation, "FROM-BFF", "Flag indicating request from BFF", "true");
    }

    private void addGlobalHeader(Operation operation, String name, String desc,
            String example) {
        Parameter parameter = new Parameter()
                .name(name)
                .description(desc)
                .in("header")
                .required(false)
                .example(example)
                .schema(new Schema<String>().type("string"));
        operation.addParametersItem(parameter);
    }

}
