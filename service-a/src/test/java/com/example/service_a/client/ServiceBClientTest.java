package com.example.service_a.client;

import com.example.service_a.config.RetryConfig;
import com.example.service_a.service.RetryService;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceBClientTest {

    @Test
    void shouldBuildServiceBXmlEndpoint() {

        RetryConfig retryConfig = new RetryConfig(3, 0);
        RetryService retryService = new RetryService(retryConfig);

        ServiceBClient client =
                new ServiceBClient("http://localhost:8081",
                        retryService,
                        HttpClient.newHttpClient(),
                        5000
                );

        assertEquals(
                "http://localhost:8081/api/xml",
                client.getXmlEndpoint()
        );
    }
}
