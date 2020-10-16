package com.dynabyte.marleyrest.api.exception;

/**
 * Exception for when a request has an invalid argument
 */
public class InvalidArgumentException extends RuntimeException {

    public InvalidArgumentException(String message) {
        super(message);
    }

}
