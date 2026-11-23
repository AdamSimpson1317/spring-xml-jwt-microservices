package com.example.service_a.exception;

public class RetryableServiceBException extends RuntimeException {
    public RetryableServiceBException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetryableServiceBException(String message) {
        super(message);
    }
}
