package com.example.j2n.auth_srv.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a specific HTTP response status.
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface J2NApiResponse {
    /**
     * HTTP Status code (e.g., 200, 400).
     */
    int httpCode() default 200;

    /**
     * Overall description of this response status.
     */
    String description() default "";

    /**
     * List of examples/scenarios for this status.
     */
    J2NApiExample[] examples() default {};
}
