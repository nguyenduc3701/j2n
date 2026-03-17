package com.example.j2n.auth_srv.swagger;

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
     * 
     * @return required permissions or roles
     */
    @AliasFor("value")
    String[] roles() default {};

    /**
     * Required permissions or roles.
     * 
     * @return array of permission strings (usually from PermissionConst)
     */
    @AliasFor("roles")
    String[] value() default {};
}
