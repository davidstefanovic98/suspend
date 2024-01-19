package com.suspend.exception;

public class InvalidAnnotationException extends RuntimeException {

    public InvalidAnnotationException(String message) {
        super(message);
    }

    public InvalidAnnotationException(String message, Throwable cause) {
        super(message, cause);
    }
}
