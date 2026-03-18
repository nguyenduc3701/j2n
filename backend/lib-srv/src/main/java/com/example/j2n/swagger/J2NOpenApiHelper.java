package com.example.j2n.swagger;

import com.example.j2n.impl.BaseMessage;
import com.example.j2n.swagger.annotation.J2NApiExample;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.examples.Example;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Utility class for common OpenAPI/Swagger processing logic.
 * Contains methods for auto-generating examples and resolving MessageEnums via reflection.
 */
public class J2NOpenApiHelper {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Creates a standard OpenAPI object with security schemes and basic info.
     * Used to reduce boilerplate in OpenApiConfig across microservices.
     */
    public static OpenAPI createDefaultOpenAPI(String title, String version, String description) {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new Info()
                        .title(title)
                        .version(version)
                        .description(description));
    }

    /**
     * Creates a Swagger Example object using information from @J2NApiExample annotation.
     */
    public static Example createSwaggerExample(J2NApiExample annotation, HandlerMethod handlerMethod, Object dataExample) {
        BaseMessage baseMessage = getBaseMessage(annotation, handlerMethod);
        String code = baseMessage.getCode();
        String msg = baseMessage.getMessage();
        if (annotation.args().length > 0) {
            msg = String.format(msg, (Object[]) annotation.args());
        }

        return createSwaggerExample(annotation.summary(), code, msg, dataExample, annotation.value());
    }

    /**
     * Low-level helper to construct a Swagger Example object.
     * Follows the structure: { "code": "...", "data": ..., "message": "..." }
     */
    public static Example createSwaggerExample(String summary, String code, String msg, Object dataExample, String rawValue) {
        Example example = new Example();
        example.setSummary(summary.isEmpty() ? msg : summary);

        // If raw JSON value is provided in the annotation, use it directly
        if (rawValue != null && !rawValue.isEmpty()) {
            example.setValue(parseJsonValue(rawValue));
        } else {
            // Otherwise, build the standard response body map
            Map<String, Object> exampleMap = new LinkedHashMap<>();
            exampleMap.put("code", code);
            exampleMap.put("data", dataExample);
            exampleMap.put("message", msg);
            example.setValue(exampleMap);
        }
        return example;
    }

    /**
     * Resolves the correct BaseMessage for an annotation.
     * Supports both direct Enum reference (baseResponseStatus)
     * and String lookup (status) for MessageConstants.
     */
    public static BaseMessage getBaseMessage(J2NApiExample annotation, HandlerMethod handlerMethod) {
        if (annotation.status() != null && !annotation.status().isEmpty()) {
            BaseMessage resolved = resolveMessageEnum(annotation.status(), handlerMethod);
            if (resolved != null) {
                return resolved;
            }
        }
        return annotation.baseResponseStatus();
    }

    /**
     * Uses Reflection to find a MessageEnum class in the microservice's package.
     * Matches the 'statusName' against either the Enum constant name OR its message string.
     * This enables using MessageEnum.MessageConstants.SYMBOL in annotations.
     */
    private static BaseMessage resolveMessageEnum(String statusName, HandlerMethod handlerMethod) {
        try {
            Class<?> controllerClass = handlerMethod.getBeanType();
            String basePackage = controllerClass.getPackageName();

            // Look for MessageEnum in common locations relative to the controller
            String[] possibleClasses = {
                basePackage + ".constant.MessageEnum",
                basePackage.substring(0, basePackage.lastIndexOf('.')) + ".constant.MessageEnum",
                basePackage + ".MessageEnum"
            };

            for (String className : possibleClasses) {
                try {
                    Class<?> enumClass = Class.forName(className);
                    if (enumClass.isEnum()) {
                        for (Object constant : enumClass.getEnumConstants()) {
                            if (constant instanceof BaseMessage) {
                                BaseMessage bm = (BaseMessage) constant;
                                // Check if statusName matches Enum name (TOKEN_INVALID)
                                // or Message content ("Invalid token") from MessageConstants
                                if (constant.toString().equals(statusName) || statusName.equals(bm.getMessage())) {
                                    return bm;
                                }
                            }
                        }
                    }
                } catch (ClassNotFoundException ignored) {}
            }
        } catch (Exception e) {
            // Silently fail and use fallback
        }
        return null;
    }

    /**
     * Auto-reflector to generate an example object for the 'data' field
     * based on the return type of the controller method.
     */
    public static Object resolveDataExample(HandlerMethod handlerMethod) {
        try {
            // Dig into ResponseEntity<BaseResponse<T>> to get T
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
            // Fail gracefully
        }
        return null;
    }

    /**
     * Recursively resolves a Type into a mock example object (Map for DTOs, List for Collections).
     */
    private static Object resolveTypeExample(Type type, Set<Class<?>> visited) {
        if (type instanceof Class) {
            return createExampleFromClass((Class<?>) type, visited);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            // Handle List<T>
            if (pt.getRawType().equals(List.class)) {
                Type itemType = pt.getActualTypeArguments()[0];
                Object itemExample = resolveTypeExample(itemType, visited);
                return itemExample != null ? Collections.singletonList(itemExample) : new ArrayList<>();
            }
        }
        return null;
    }

    /**
     * Creates a Map representing a DTO class.
     * Respects @Schema(example = "...") annotations if present.
     */
    private static Object createExampleFromClass(Class<?> clazz, Set<Class<?>> visited) {
        if (clazz == null)
            return null;
        if (clazz.equals(String.class))
            return "";
        if (Number.class.isAssignableFrom(clazz) || clazz.isPrimitive())
            return 0;

        // Prevent infinite recursion for cyclic references
        if (visited.contains(clazz))
            return null;

        visited.add(clazz);
        try {
            Map<String, Object> map = new HashMap<>();
            for (Field field : clazz.getDeclaredFields()) {
                // If the field has a Swagger @Schema example, use it
                io.swagger.v3.oas.annotations.media.Schema schema = field
                        .getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class);
                if (schema != null && !schema.example().isEmpty()) {
                    map.put(field.getName(), parseJsonValue(schema.example()));
                } else {
                    // Otherwise, recurse
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

    /**
     * Safely parses a string value (possibly JSON) into an object/tree.
     */
    private static Object parseJsonValue(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        try {
            return OBJECT_MAPPER.readTree(value);
        } catch (Exception e) {
            return value;
        }
    }
}
