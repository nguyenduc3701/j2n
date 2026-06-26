package com.example.j2n.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidNotEmptyValidatorTest {

    private ValidNotEmptyValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ValidNotEmptyValidator();
        context = null; // Mock context is not utilized by the validator logic
    }

    @Test
    void testIsValid_NullRequest_ReturnsFalse() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    void testIsValid_EmptyRequest_ReturnsFalse() {
        ValidatableRequest emptyRequest = () -> true;
        assertFalse(validator.isValid(emptyRequest, context));
    }

    @Test
    void testIsValid_NonEmptyRequest_ReturnsTrue() {
        ValidatableRequest nonEmptyRequest = () -> false;
        assertTrue(validator.isValid(nonEmptyRequest, context));
    }
}
