package com.example.service_a.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RetryConfig {

    private final int maxAttempts;
    private final long delayMs;

    public RetryConfig(
            @Value("${service-b.retry.max-attempts}") int maxAttempts,
            @Value("${service-b.retry.delay-ms}") long delayMs) {

        this.maxAttempts = maxAttempts;
        this.delayMs = delayMs;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public long getDelayMs() {
        return delayMs;
    }
}
