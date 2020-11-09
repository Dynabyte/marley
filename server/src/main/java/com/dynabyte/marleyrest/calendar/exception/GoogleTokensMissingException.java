package com.dynabyte.marleyrest.calendar.exception;

public class GoogleTokensMissingException extends RuntimeException {

    public GoogleTokensMissingException(String message) {
        super(message);
    }
}
