package com.example.service_a.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTest {

    @Test
    void shouldReturn502ForServiceBClientException() {

        GlobalExceptionHandler handler =
                new GlobalExceptionHandler();

        ServiceBClientException exception =
                new ServiceBClientException(
                        "Service B returned HTTP 400"
                );

        ResponseEntity<String> response =
                handler.handleServiceBClientException(exception);

        assertEquals(
                HttpStatus.BAD_GATEWAY,
                response.getStatusCode()
        );
    }

    @Test
    void shouldReturn503ForRetryExhaustedException() {

        GlobalExceptionHandler handler =
                new GlobalExceptionHandler();

        RetryExhaustedException exception =
                new RetryExhaustedException(
                        "Operation failed after maximum retry attempts",
                        new RuntimeException("Service B unavailable")
                );

        ResponseEntity<String> response =
                handler.handleRetryExhaustedException(exception);

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                response.getStatusCode()
        );

        assertEquals(
                "Operation failed after maximum retry attempts",
                response.getBody()
        );
    }
}
