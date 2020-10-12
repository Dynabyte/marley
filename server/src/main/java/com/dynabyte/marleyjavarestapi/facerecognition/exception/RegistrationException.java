package com.dynabyte.marleyjavarestapi.facerecognition.exception;

/**
 * Exception for when a person or image cannot be registered
 */
public class RegistrationException extends RuntimeException {

    public RegistrationException(String message) {
        super(message);
    }

}
