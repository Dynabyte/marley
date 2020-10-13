package com.dynabyte.marleyjavarestapi.facerecognition.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Getter
public class FaceRecognitionException extends IOException {

    private final HttpStatus httpStatus;

    public FaceRecognitionException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
