package com.dynabyte.marleyrest.api.exception;

import com.dynabyte.marleyrest.calendar.exception.GoogleAPIException;
import com.dynabyte.marleyrest.calendar.exception.GoogleCredentialsMissingException;
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
import org.springframework.web.client.ResourceAccessException;

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
    @ExceptionHandler(value = {ImageEncodingException.class, InvalidArgumentException.class, RequestBodyNotFoundException.class})
    public ResponseEntity<ApiExceptionReport> handleBadRequestExceptions(Exception e) {
        return getErrorResponse(e, HttpStatus.BAD_REQUEST);
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
        return getErrorResponse(e, HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Handles exception thrown from external face recognition API
     *
     * @param e The thrown exception
     * @return ResponseEntity including an ApiExceptionReport object that details the error as well as the http status.
     */
    @ExceptionHandler(value = {FaceRecognitionException.class})
    public ResponseEntity<ApiExceptionReport> handleExternalAPIException(FaceRecognitionException e) {
         return getErrorResponse(e, e.getHttpStatus());
    }


    /**
     * Handles exception when external API is unavailable
     * @param e The thrown exception
     * @return ResponseEntity including an ApiExceptionReport object that details the error as well as the http status.
     */
    @ExceptionHandler(value = {ResourceAccessException.class})
    public ResponseEntity<ApiExceptionReport> handleExternalAPIException(ResourceAccessException e){
        return getErrorResponse(e, HttpStatus.SERVICE_UNAVAILABLE, "Face recognition service unavailable");
    }

    /**
     * Handles internal issues that can occur if credentials are not properly set up or if Google API throws Exception
     * @param e The thrown exception
     * @return ResponseEntity including an ApiExceptionReport object that details the error as well as the http status.
     */
    @ExceptionHandler(value = {GoogleAPIException.class, GoogleCredentialsMissingException.class})
    public ResponseEntity<ApiExceptionReport> handleGoogleRelatedException(Exception e){
        return getErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Generates a ResponseEntity with a built in ApiExceptionReport.
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

    /**
     * Generates a ResponseEntity with a built in ApiExceptionReport with a custom message.
     *
     * @param e          The exception thrown
     * @param httpStatus Suitable http status for the exception
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
        LOGGER.warn(String.valueOf(apiExceptionReport));
        return new ResponseEntity<>(apiExceptionReport, httpStatus);
    }
}
