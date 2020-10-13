package com.dynabyte.marleyrest.prediction.exception;

import org.springframework.http.HttpStatus;

public class FaceNotFoundException extends FaceRecognitionException {

    public FaceNotFoundException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
