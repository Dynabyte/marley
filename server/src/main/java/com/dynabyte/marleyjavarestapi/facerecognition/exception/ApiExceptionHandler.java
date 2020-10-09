package com.dynabyte.marleyjavarestapi.facerecognition.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * This class handles api errors listed in "value" for the @ExceptionHandler annotation and displays structured information to the api user.
 */
@ControllerAdvice
public class ApiExceptionHandler {

    /**
     * Handles exceptions in the api, creating a ResponseEntity object which gives the api user better information than just an internal server error.
     * All the exception types that are handled are for bad requests and listed in "value" for the @ExceptionHandler annotation
     * @param e The thrown exception
     * @return ResponseEntity including an ApiExceptionReport object that details the error as well as the http status.
     */
    @ExceptionHandler(value = {ImageEncodingException.class, MissingArgumentException.class, InvalidArgumentException.class})
    public ResponseEntity<ApiExceptionReport> handleBadRequestExceptions(Exception e){
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return getErrorResponse(e, httpStatus, e.getMessage());
    }

    /**
     * Handles exceptions in the api, creating a ResponseEntity object which gives the api user better information than just an internal server error.
     * All the exception types that are handled are for internal server errors and listed in "value" for the @ExceptionHandler annotation
     * @param e The thrown exception
     * @return ResponseEntity including an ApiExceptionReport object that details the error as well as the http status.
     */
    @ExceptionHandler(value = {MissingPersonInDbException.class, PersonAlreadyInDbException.class, RegistrationException.class})
    public ResponseEntity<ApiExceptionReport> handleCustomInternalExceptions(Exception e){
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return getErrorResponse(e, httpStatus, e.getMessage());
    }

    /**
     * Handles exception thrown from external API
     * @param e The thrown exception
     * @return ResponseEntity including an ApiExceptionReport object that details the error as well as the http status.
     */
    @ExceptionHandler(value = {HttpServerErrorException.class})
    public ResponseEntity<ApiExceptionReport> handleExternalAPIException(Exception e){
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return getErrorResponse(e, httpStatus, "Error in FaceRecognition API");
    }

    /**
     * Generates a ResponseEntity with a build in ApiExceptionReport.
     * @param e The exception thrown
     * @param httpStatus Suitable http status for the exception
     * @param message Custom message for the exception report
     * @return ResponseEntity which includes an ApiExceptionReport
     */
    private ResponseEntity<ApiExceptionReport> getErrorResponse(Exception e, HttpStatus httpStatus, String message) {
        String exceptionClass = e.getClass().getSimpleName();
        ApiExceptionReport apiExceptionReport = new ApiExceptionReport(
                exceptionClass,
                message,
                httpStatus,
                ZonedDateTime.now(ZoneId.of("+02:00"))
        );
        return new ResponseEntity<>(apiExceptionReport, httpStatus);
    }
}
