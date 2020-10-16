package com.dynabyte.marleyrest.prediction.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FaceRecognitionException extends RuntimeException {

    private final HttpStatus httpStatus;

    public FaceRecognitionException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
