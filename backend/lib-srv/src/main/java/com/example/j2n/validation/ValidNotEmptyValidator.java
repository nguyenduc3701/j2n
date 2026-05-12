package com.example.j2n.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator executing logic for @ValidNotEmpty annotation.
 * Validates that any request implementing ValidatableRequest returns false for isEmpty().
 */
public class ValidNotEmptyValidator implements ConstraintValidator<ValidNotEmpty, ValidatableRequest> {

    @Override
    public boolean isValid(ValidatableRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return false;
        }
        return !request.isEmpty();
    }
}
