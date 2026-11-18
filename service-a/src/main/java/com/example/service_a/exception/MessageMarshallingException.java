package com.example.service_a.exception;

public class MessageMarshallingException extends RuntimeException {
    public MessageMarshallingException(String message, Throwable cause) {
        super(message, cause);
    }
}
