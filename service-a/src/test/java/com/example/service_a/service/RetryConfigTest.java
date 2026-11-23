package com.example.service_a.service;

import com.example.service_a.config.RetryConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RetryConfigTest {

    @Test
    void shouldStoreRetryConfiguration() {

        RetryConfig retryConfig = new RetryConfig(3, 2000);

        assertEquals(3, retryConfig.getMaxAttempts());
        assertEquals(2000, retryConfig.getDelayMs());
    }

}
