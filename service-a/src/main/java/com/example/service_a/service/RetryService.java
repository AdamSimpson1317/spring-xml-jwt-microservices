package com.example.service_a.service;

import com.example.service_a.config.RetryConfig;
import com.example.service_a.exception.RetryExhaustedException;
import com.example.service_a.exception.RetryableServiceBException;
import com.example.service_a.exception.ServiceBClientException;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

@Service
public class RetryService {

    private final RetryConfig retryConfig;

    public RetryService(RetryConfig retryConfig) {
        this.retryConfig = retryConfig;
    }

    public <T> T execute(Callable<T> operation) {

        int attempts = 0;

        while (attempts < retryConfig.getMaxAttempts()) {

            try {
                attempts++;

                return operation.call();

            } catch (RetryableServiceBException exception) {

                if (attempts >= retryConfig.getMaxAttempts()) {
                    throw new RetryExhaustedException(
                            "Operation failed after maximum retry attempts",
                            exception
                    );
                }

                waitBeforeRetry();

            } catch (ServiceBClientException exception) {

                throw exception;

            } catch (Exception exception) {

                throw new RuntimeException(
                        "Operation failed",
                        exception
                );
            }
        }

        throw new IllegalStateException("Retry loop completed unexpectedly");
    }

    private void waitBeforeRetry() {

        try {
            Thread.sleep(retryConfig.getDelayMs());

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Retry interrupted",
                    exception
            );
        }
    }
}
