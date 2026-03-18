package com.example.j2n.swagger;

import com.example.j2n.constants.CommonConst;
import com.example.j2n.impl.BaseMessage;
import com.example.j2n.swagger.annotation.J2NApiExample;
import com.example.j2n.swagger.annotation.J2NApiResponse;
import com.example.j2n.swagger.annotation.J2NApiResponses;
import com.example.j2n.swagger.annotation.J2NApiRole;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Customizer for OpenAPI Operation objects.
 * This class processes custom J2N annotations to enrich the Swagger/OpenAPI documentation.
 */
@Component
public class J2NOpenApiCustomizer implements OperationCustomizer {

    /**
     * Main entry point for customizing an operation.
     * 
     * @param operation The OpenAPI operation object
     * @param handlerMethod The controller method being processed
     * @return The customized operation
     */
    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        if (operation.getParameters() == null) {
            operation.setParameters(new ArrayList<>());
        }

        // 1. Add Global Headers (UserId, UserName, etc.) for all endpoints
        addGlobalHeader(operation, CommonConst.X_USER_ID, "User ID from Gateway/BFF", "60a7b1b5e4b0a1a2b3c4d5e6");
        addGlobalHeader(operation, CommonConst.X_USER_NAME, "Username from Gateway/BFF", "admin");
        addGlobalHeader(operation, CommonConst.X_ROLE_ID, "Role ID from Gateway/BFF", "ROLE_ADMIN");
        addGlobalHeader(operation, "FROM-BFF", "Flag indicating request from BFF", "true");

        // 2. Process custom J2N annotations
        applyRolePermissions(operation, handlerMethod);
        applyRequestBodyExample(operation, handlerMethod); // For @PostMapping etc.
        applyCustomResponses(operation, handlerMethod);   // For @J2NApiResponses

        return operation;
    }

    /**
     * Adds a header parameter to the operation if it doesn't already exist.
     */
    private void addGlobalHeader(Operation operation, String name, String desc, String example) {
        boolean exists = operation.getParameters().stream()
                .anyMatch(p -> name.equals(p.getName()));

        if (!exists) {
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

    /**
     * Processes @J2NApiRole annotation to add "Required Permissions" information 
     * to the operation description.
     */
    private void applyRolePermissions(Operation operation, HandlerMethod handlerMethod) {
        J2NApiRole roleAnnotation = handlerMethod.getMethodAnnotation(J2NApiRole.class);
        if (roleAnnotation == null) {
            roleAnnotation = handlerMethod.getBeanType().getAnnotation(J2NApiRole.class);
        }

        if (roleAnnotation != null) {
            String[] rolesArray = roleAnnotation.roles().length > 0 ? roleAnnotation.roles() : roleAnnotation.value();
            String roles = Arrays.stream(rolesArray).collect(Collectors.joining(", "));

            String description = operation.getDescription() != null ? operation.getDescription() : "";
            // Append permissions to the end of the description with HTML formatting,
            // but only if it's not already there (idempotency check)
            if (!description.contains("Required Permissions:")) {
                operation.setDescription(
                        String.format("%s<br><br><br>Required Permissions: [ <b>%s</b> ]", description, roles));
            }
        }
    }

    /**
     * Processes @J2NApiExample at the method level to provide a custom Request Body example.
     */
    private void applyRequestBodyExample(Operation operation, HandlerMethod handlerMethod) {
        J2NApiExample exampleAnnotation = handlerMethod.getMethodAnnotation(J2NApiExample.class);
        if (exampleAnnotation != null && operation.getRequestBody() != null) {
            Content content = operation.getRequestBody().getContent();
            if (content != null) {
                // Resolve the MessageEnum constant (via name or message string)
                BaseMessage baseMessage = J2NOpenApiHelper.getBaseMessage(exampleAnnotation, handlerMethod);
                String code = baseMessage.getCode();
                String msg = baseMessage.getMessage();
                if (exampleAnnotation.args().length > 0) {
                    msg = String.format(msg, (Object[]) exampleAnnotation.args());
                }
                final String finalMsg = msg;

                // Auto-generate the 'data' field example from the request DTO schema
                Object dataExample = J2NOpenApiHelper.resolveDataExample(handlerMethod);

                content.forEach((mediaTypeKey, mediaType) -> {
                    Example example = J2NOpenApiHelper.createSwaggerExample(
                            exampleAnnotation.summary(), code, finalMsg, dataExample, exampleAnnotation.value());
                    mediaType.addExamples("Custom Example", example);
                });
            }
        }
    }

    /**
     * Processes @J2NApiResponses and nested @J2NApiResponse annotations to create
     * multiple named examples for different success/error scenarios.
     */
    private void applyCustomResponses(Operation operation, HandlerMethod handlerMethod) {
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
                // Determine the code and message based on annotation values or MessageEnum lookup
                BaseMessage baseMessage = J2NOpenApiHelper.getBaseMessage(example, handlerMethod);
                String code = baseMessage.getCode();
                String msg = baseMessage.getMessage();
                if (example.args().length > 0) {
                    msg = String.format(msg, (Object[]) example.args());
                }

                // Append the list of possible codes to the main response description
                descBuilder.append("- <b>").append(code).append("</b>: ").append(msg).append("<br>");

                // Auto-generate 'data' field for success codes (2xx)
                Object dataExample = (item.httpCode() == 200 || item.httpCode() == 201)
                        ? J2NOpenApiHelper.resolveDataExample(handlerMethod)
                        : null;

                Example swaggerExample = J2NOpenApiHelper.createSwaggerExample(
                        example.summary(), code, msg, dataExample, example.value());
                
                // Use the Enum name or code as the key in the Swagger UI dropdown
                String exampleKey = generateExampleKey(example, handlerMethod);
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

    /**
     * Generates a unique key for each example to be displayed in the Swagger UI.
     * Prefers Enum names for better readability in the UI.
     */
    private String generateExampleKey(J2NApiExample example, HandlerMethod handlerMethod) {
        BaseMessage baseMessage = J2NOpenApiHelper.getBaseMessage(example, handlerMethod);
        String key = baseMessage instanceof Enum ? ((Enum<?>) baseMessage).name() : baseMessage.getCode();
        if (example.args().length > 0) {
            key += "_" + String.join("_", example.args());
        }
        return key;
    }
}
