package com.example.j2n.auth_srv.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define a single API response item for Swagger/OpenAPI
 * documentation.
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface J2NApiResponseItem {
    /**
     * HTTP Status code (e.g., "200", "400", "500").
     * 
     * @return status code
     */
    String status() default "200";

    /**
     * Internal business code (e.g., from MessageEnum).
     * 
     * @return business code
     */
    String code() default "";

    /**
     * Description or message of the response.
     * 
     * @return message
     */
    String message() default "";

    /**
     * Description for the Swagger UI.
     * 
     * @return description
     */
    String description() default "";
}
