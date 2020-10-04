package com.dynabyte.marleyjavarestapi.facerecognition.exception;

/**
 * Exception for when an argument is missing in an incoming request
 */
public class MissingArgumentException extends RuntimeException {

    public MissingArgumentException(String message) {
        super(message);
    }
}
