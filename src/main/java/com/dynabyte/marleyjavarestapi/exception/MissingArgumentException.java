package com.dynabyte.marleyjavarestapi.exception;

import org.springframework.web.bind.MissingServletRequestParameterException;

public class MissingArgumentException extends RuntimeException {

    public MissingArgumentException(String message) {
        super(message);
    }
}
