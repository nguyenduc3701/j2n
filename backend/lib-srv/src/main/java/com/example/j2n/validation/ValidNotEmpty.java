package com.example.j2n.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom class-level validation constraint ensuring a request body contains at least one field to update.
 */
@Documented
@Constraint(validatedBy = ValidNotEmptyValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNotEmpty {

    String message() default "Request body must contain at least one field to update";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
