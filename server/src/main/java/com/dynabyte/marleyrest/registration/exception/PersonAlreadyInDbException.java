package com.dynabyte.marleyrest.registration.exception;

/**
 * Exception for when trying to register a person who is already in the person database
 */
public class PersonAlreadyInDbException extends RuntimeException {

    public PersonAlreadyInDbException(String message) {
        super(message);
    }

}
