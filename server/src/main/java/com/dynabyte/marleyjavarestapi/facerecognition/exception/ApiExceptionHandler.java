package com.dynabyte.marleyjavarestapi.facerecognition.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * This class handles api errors listed in "value" for the @ExceptionHandler annotation and displays structured information to the api user.
 */
@ControllerAdvice
public class ApiExceptionHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /**
     * Handles exceptions in the api, creating a ResponseEntity object which gives the api user better information than just an internal server error.
     * All the exception types that are handled are for bad requests and listed in "value" for the @ExceptionHandler annotation
     * @param e The thrown exception
     * @return ResponseEntity including an ApiExceptionReport object that details the error as well as the http status.
     */
    @ExceptionHandler(value = {ImageEncodingException.class, InvalidArgumentException.class})
    public ResponseEntity<ApiExceptionReport> handleBadRequestExceptions(Exception e){
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return getErrorResponse(e, httpStatus);
    }

    /**
     * Handles exceptions in the api, creating a ResponseEntity object which gives the api user better information than just an internal server error.
     * All the exception types that are handled are for internal server errors and listed in "value" for the @ExceptionHandler annotation
     * @param e The thrown exception
     * @return ResponseEntity including an ApiExceptionReport object that details the error as well as the http status.
     */
    @ExceptionHandler(value = {MissingPersonInDbException.class, PersonAlreadyInDbException.class, RegistrationException.class})
    public ResponseEntity<ApiExceptionReport> handleCustomInternalExceptions(Exception e){
        HttpStatus httpStatus = HttpStatus.NOT_ACCEPTABLE;
        return getErrorResponse(e, httpStatus);
    }

    /**
     * Handles exception thrown from external API
     * @param e The thrown exception
     * @return ResponseEntity including an ApiExceptionReport object that details the error as well as the http status.
     */
    @ExceptionHandler(value = {HttpServerErrorException.class})
    public ResponseEntity<ApiExceptionReport> handleExternalAPIException(HttpServerErrorException e){
        HttpStatus httpStatus = e.getStatusCode();
        return getErrorResponse(e, httpStatus);
    }


//    /**
//     * Handles exception thrown from external API
//     * @param e The thrown exception
//     * @return ResponseEntity including an ApiExceptionReport object that details the error as well as the http status.
//     */
//    @ExceptionHandler(value = {HttpClientErrorException.class})
//    public ResponseEntity<ApiExceptionReport> handleExternalAPIException(HttpClientErrorException e){
//        HttpStatus httpStatus = e.getStatusCode();
//        return getErrorResponse(e, httpStatus);
//    }

    /**
     * Generates a ResponseEntity with a build in ApiExceptionReport.
     * @param e The exception thrown
     * @param httpStatus Suitable http status for the exception
     * @return ResponseEntity which includes an ApiExceptionReport
     */
    private ResponseEntity<ApiExceptionReport> getErrorResponse(Exception e, HttpStatus httpStatus) {
        String exceptionClass = e.getClass().getSimpleName();
        ApiExceptionReport apiExceptionReport = new ApiExceptionReport(
                exceptionClass,
                e.getMessage(),
                httpStatus,
                ZonedDateTime.now(ZoneId.of("+02:00"))
        );
        LOGGER.error(String.valueOf(apiExceptionReport));
        return new ResponseEntity<>(apiExceptionReport, httpStatus);
    }
}
