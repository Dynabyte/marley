package com.dynabyte.marleyrest.api.exception;

/**
 * Exception for when a response body is not supplied in a request
 */
public class RequestBodyNotFoundException extends RuntimeException {

    public RequestBodyNotFoundException(String message) {
        super(message);
    }
}
