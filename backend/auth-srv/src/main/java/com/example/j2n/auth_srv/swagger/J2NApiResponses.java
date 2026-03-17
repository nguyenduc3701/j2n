package com.example.j2n.auth_srv.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation for multiple {@link J2NApiResponseItem} annotations.
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface J2NApiResponses {
    /**
     * Array of API response definitions.
     */
    J2NApiResponse[] value() default {};
}
