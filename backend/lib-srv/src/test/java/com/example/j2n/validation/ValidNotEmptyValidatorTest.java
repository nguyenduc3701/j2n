package com.example.j2n.validation;

import jakarta.validation.ConstraintValidatorContext;
import junit.framework.TestCase;

public class ValidNotEmptyValidatorTest extends TestCase {

    private ValidNotEmptyValidator validator;
    private ConstraintValidatorContext context;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validator = new ValidNotEmptyValidator();
        context = null; // Mock context is not utilized by the validator logic
    }

    public void testIsValid_NullRequest_ReturnsFalse() {
        assertFalse(validator.isValid(null, context));
    }

    public void testIsValid_EmptyRequest_ReturnsFalse() {
        ValidatableRequest emptyRequest = () -> true;
        assertFalse(validator.isValid(emptyRequest, context));
    }

    public void testIsValid_NonEmptyRequest_ReturnsTrue() {
        ValidatableRequest nonEmptyRequest = () -> false;
        assertTrue(validator.isValid(nonEmptyRequest, context));
    }
}
