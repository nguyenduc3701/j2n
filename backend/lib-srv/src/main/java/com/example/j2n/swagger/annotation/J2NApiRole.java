package com.example.j2n.swagger.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define required roles or permissions for documentation in
 * Swagger/OpenAPI.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface J2NApiRole {
    /**
     * Alias for {@link #value()}.
     */
    @AliasFor("value")
    String[] roles() default {};

    /**
     * Required permissions or roles.
     */
    @AliasFor("roles")
    String[] value() default {};
}
