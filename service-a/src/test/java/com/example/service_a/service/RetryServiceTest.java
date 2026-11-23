package com.example.service_a.service;

import com.example.service_a.config.RetryConfig;
import com.example.service_a.exception.RetryableServiceBException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RetryServiceTest {

    @Test
    void shouldRetryFailedOperationUntilItSucceeds() {

        RetryConfig config = new RetryConfig(3, 0);

        RetryService retryService = new RetryService(config);

        AtomicInteger attempts = new AtomicInteger();

        String result = retryService.execute(() -> {

            int attempt = attempts.incrementAndGet();

            if (attempt < 3) {
                throw new RetryableServiceBException("Temporary failure");
            }

            return "success";
        });

        assertEquals("success", result);
        assertEquals(3, attempts.get());
    }

    @Test
    void shouldStopAfterMaximumAttempts() {

        RetryConfig config = new RetryConfig(3, 0);
        RetryService retryService = new RetryService(config);

        AtomicInteger attempts = new AtomicInteger();

        assertThrows(
                RuntimeException.class,
                () -> retryService.execute(() -> {
                    attempts.incrementAndGet();
                    throw new RetryableServiceBException("Temporary failure");
                })
        );

        assertEquals(3, attempts.get());
    }

    @Test
    void shouldNotRetryNonRetryableException() {

        RetryConfig config = new RetryConfig(3, 0);
        RetryService retryService = new RetryService(config);

        AtomicInteger attempts = new AtomicInteger();

        assertThrows(
                RuntimeException.class,
                () -> retryService.execute(() -> {
                    attempts.incrementAndGet();
                    throw new IllegalArgumentException("Bad request");
                })
        );

        assertEquals(1, attempts.get());
    }
}