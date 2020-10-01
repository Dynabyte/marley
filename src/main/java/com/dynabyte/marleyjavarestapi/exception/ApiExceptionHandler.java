package com.dynabyte.marleyjavarestapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * This class handles api errors listed in "value" for the @ExceptionHandler annotation and displays structured information to the api user.
 */
@ControllerAdvice
public class ApiExceptionHandler {

    /**
     * Handles exceptions in the api, creating a ResponseEntity object which gives the api user better information than just an internal server error.
     * All the exception types that are handled are listed in "value" for the @ExceptionHandler annotation
     * @param e The thrown exception
     * @return ResponseEntity including an ApiException object that details the error as well as the http status.
     */
    @ExceptionHandler(value = {ImageEncodingException.class, MissingArgumentException.class})
    public ResponseEntity<Object> handleImageEncodingException(Exception e){
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String exceptionClass = e.getClass().toString().substring(47);
        ApiExceptionReport apiExceptionReport = new ApiExceptionReport(
                exceptionClass,
                e.getMessage(),
                httpStatus,
                ZonedDateTime.now(ZoneId.of("+02:00"))
        );
        return new ResponseEntity<>(apiExceptionReport, httpStatus);
    }
}