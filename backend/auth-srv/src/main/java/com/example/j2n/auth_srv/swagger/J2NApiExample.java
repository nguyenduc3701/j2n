package com.example.j2n.auth_srv.swagger;

import com.example.j2n.auth_srv.constant.MessageEnum;
import com.example.j2n.enums.BaseMessageEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a specific response scenario based on MessageEnum.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface J2NApiExample {
    /**
     * The business status enum from auth-srv.
     */
    MessageEnum responseStatus() default MessageEnum.NOT_SET;

    /**
     * The general business status enum from lib-srv.
     */
    BaseMessageEnum baseResponseStatus() default BaseMessageEnum.SUCCESS;

    /**
     * Arguments to format the message from responseStatus.
     */
    String[] statusMessageArgs() default {};

    /**
     * A short summary for this example.
     */
    String summary() default "";

    /**
     * Raw JSON value if you want to override the default response body.
     */
    String value() default "";

}
