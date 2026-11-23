package com.example.service_a.exception;

public class RetryExhaustedException extends RuntimeException {

    public RetryExhaustedException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}
