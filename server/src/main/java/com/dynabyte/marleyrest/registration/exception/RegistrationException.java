package com.dynabyte.marleyrest.registration.exception;

/**
 * Exception for when a person cannot be registered to the database
 */
public class RegistrationException extends RuntimeException {

    public RegistrationException(String message) {
        super(message);
    }
}
