package com.dynabyte.marleyrest.api.exception;

import com.dynabyte.marleyrest.deletion.exception.IdNotFoundException;
import com.dynabyte.marleyrest.prediction.exception.*;
import com.dynabyte.marleyrest.registration.exception.MissingPersonInDbException;
import com.dynabyte.marleyrest.registration.exception.PersonAlreadyInDbException;
import com.dynabyte.marleyrest.registration.exception.RegistrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /**
     * Handles exceptions in the api, creating a ResponseEntity object which gives the api user better information than just an internal server error.
     * All the exception types that are handled are for bad requests and listed in "value" for the @ExceptionHandler annotation
     *
     * @param e The thrown exception
     * @return ResponseEntity including an ApiExceptionReport object that details the error as well as the http status.
     */
    @ExceptionHandler(value = {ImageEncodingException.class, InvalidArgumentException.class, ResponseBodyNotFoundException.class})
    public ResponseEntity<ApiExceptionReport> handleBadRequestExceptions(Exception e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return getErrorResponse(e, httpStatus);
    }

    /**
     * Handles exceptions in the api, creating a ResponseEntity object which gives the api user better information than just an internal server error.
     * All the exception types that are handled are for not acceptable requests and listed in "value" for the @ExceptionHandler annotation
     *
     * @param e The thrown exception
     * @return ResponseEntity including an ApiExceptionReport object that details the error as well as the http status.
     */
    @ExceptionHandler(value = {MissingPersonInDbException.class, PersonAlreadyInDbException.class, RegistrationException.class, IdNotFoundException.class})
    public ResponseEntity<ApiExceptionReport> handleCustomInternalExceptions(Exception e) {
        HttpStatus httpStatus = HttpStatus.NOT_ACCEPTABLE;
        return getErrorResponse(e, httpStatus);
    }

    /**
     * Handles exception thrown from external face recognition API
     *
     * @param e The thrown exception
     * @return ResponseEntity including an ApiExceptionReport object that details the error as well as the http status.
     */
    @ExceptionHandler(value = {FaceRecognitionException.class})
    public ResponseEntity<ApiExceptionReport> handleExternalAPIException(FaceRecognitionException e) {
        HttpStatus httpStatus = e.getHttpStatus();
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
     *
     * @param e          The exception thrown
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
        LOGGER.warn(String.valueOf(apiExceptionReport));
        return new ResponseEntity<>(apiExceptionReport, httpStatus);
    }
}
