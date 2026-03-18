package com.example.j2n.swagger.annotation;

import com.example.j2n.enums.BaseMessageEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Shared annotation for API examples.
 * Uses a String 'status' to refer to service-specific MessageEnum constants.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface J2NApiExample {
    /**
     * The name of the Enum constant in the service's MessageEnum.
     * If not provided, it will fallback to baseResponseStatus.
     */
    String status() default "";

    /**
     * The general business status enum from lib-srv.
     */
    BaseMessageEnum baseResponseStatus() default BaseMessageEnum.SUCCESS;

    /**
     * Arguments to format the message.
     */
    String[] args() default {};

    /**
     * A short summary for this example.
     */
    String summary() default "";

    /**
     * Raw JSON value if you want to override the default response body.
     */
    String value() default "";
}
