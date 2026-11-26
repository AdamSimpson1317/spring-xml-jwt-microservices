package com.example.service_a.exception;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidMessageException.class)
    public ResponseEntity<String> handleInvalidMessageException(InvalidMessageException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(MessageMarshallingException.class)
    public ResponseEntity<String> handleMessageMarshallingException(MessageMarshallingException exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    @ExceptionHandler(MalformedXmlException.class)
    public ResponseEntity<String> handleMalformedXmlException(MalformedXmlException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(RetryExhaustedException.class)
    public ResponseEntity<String> handleRetryExhaustedException(RetryExhaustedException exception) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(exception.getMessage());
    }

    @ExceptionHandler(ServiceBClientException.class)
    public ResponseEntity<String> handleServiceBClientException(ServiceBClientException exception) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(exception.getMessage());
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<String> handleCallNotPermittedException(
            CallNotPermittedException exception) {

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Service B is temporarily unavailable");
    }
}
