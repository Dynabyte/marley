package com.dynabyte.marleyjavarestapi.exception;

/**
 * An exception that gets thrown when an image is not in required base64 format
 */
public class ImageEncodingException extends RuntimeException {

    public ImageEncodingException(String message) {
        super(message);
    }

    public ImageEncodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
