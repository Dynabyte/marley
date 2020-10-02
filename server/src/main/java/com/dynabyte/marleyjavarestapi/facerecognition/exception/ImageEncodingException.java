package com.dynabyte.marleyjavarestapi.facerecognition.exception;

/**
 * An exception that gets thrown when an image is not in required base64 format
 */
public class ImageEncodingException extends RuntimeException {

    public ImageEncodingException(String message) {
        super(message);
    }

    //Not used but throwable might be necessary to give more information later
    public ImageEncodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
