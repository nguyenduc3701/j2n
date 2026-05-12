package com.example.j2n.validation;

/**
 * Interface defining a request that can be checked for being empty.
 * Implemented by update/patch request DTOs to support automated class-level validation.
 */
public interface ValidatableRequest {

    /**
     * Check if the request body contains no fields to update.
     *
     * @return true if empty, false otherwise
     */
    boolean isEmpty();
}
