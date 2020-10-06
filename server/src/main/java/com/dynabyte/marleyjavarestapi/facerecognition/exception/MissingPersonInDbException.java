package com.dynabyte.marleyjavarestapi.facerecognition.exception;

/**
 * Exception for when a faceId is found during a prediction but a corresponding person is missing in the database
 */
public class MissingPersonInDbException extends RuntimeException {

    public MissingPersonInDbException(String message) {
        super(message);
    }

}
