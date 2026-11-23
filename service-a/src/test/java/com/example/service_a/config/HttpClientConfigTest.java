package com.example.service_a.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpClientConfigTest {

    @Test
    void shouldStoreHttpTimeoutConfiguration() {

        HttpClientConfig config =
                new HttpClientConfig(2000, 5000);

        assertEquals(2000, config.getConnectTimeoutMs());
        assertEquals(5000, config.getRequestTimeoutMs());
    }
}
