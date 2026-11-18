package com.example.service_b.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MalformedXmlException.class)
    public ResponseEntity<String> handleMalformedXmlException(MalformedXmlException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid XML");
    }

    @ExceptionHandler(InvalidMessageException.class)
    public ResponseEntity<String> handleInvalidMessageException(InvalidMessageException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
