package com.dynabyte.marleyrest.api.exception;

/**
 * Exception for when a response body is not supplied in a request
 */
public class ResponseBodyNotFoundException extends RuntimeException {

    public ResponseBodyNotFoundException(String message) {
        super(message);
    }
}
