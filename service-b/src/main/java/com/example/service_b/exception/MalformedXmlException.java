package com.example.service_b.exception;

public class MalformedXmlException extends RuntimeException{

    public MalformedXmlException(String message, Throwable cause){
        super(message,cause);
    }
}
