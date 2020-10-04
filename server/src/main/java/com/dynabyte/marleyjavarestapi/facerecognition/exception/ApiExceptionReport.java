package com.dynabyte.marleyjavarestapi.facerecognition.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

/**
 * This class is a report that gets served to the api user when certain exceptions occur during a request.
 */
@Getter
public class ApiExceptionReport {
    private final String exceptionClass;
    private final String message;
    private final HttpStatus httpStatus;
    private final ZonedDateTime timestamp;

    public ApiExceptionReport(String exceptionClass, String message, HttpStatus httpStatus, ZonedDateTime timestamp) {
        this.exceptionClass = exceptionClass;
        this.message = message;
        this.httpStatus = httpStatus;
        this.timestamp = timestamp;
    }

}
