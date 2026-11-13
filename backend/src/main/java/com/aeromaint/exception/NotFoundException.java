package com.aeromaint.exception;

/** The requested object does not exist. Returned as 404 Not Found. */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
