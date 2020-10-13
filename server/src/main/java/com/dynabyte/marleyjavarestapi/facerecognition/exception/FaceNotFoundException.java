package com.dynabyte.marleyjavarestapi.facerecognition.exception;

import org.springframework.http.HttpStatus;

import java.io.IOException;

public class FaceNotFoundException extends FaceRecognitionException {

    public FaceNotFoundException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
