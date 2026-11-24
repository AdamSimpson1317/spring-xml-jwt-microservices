package com.example.service_a.client;

import com.example.service_a.config.RetryConfig;
import com.example.service_a.service.RetryService;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServiceBClientIntegrationTest {

    @Test
    void shouldSendXmlWithCorrelationIdToServiceB() {

        RetryConfig retryConfig = new RetryConfig(3, 0);
        RetryService retryService = new RetryService(retryConfig);

        ServiceBClient client =
                new ServiceBClient("http://localhost:8081",
                        retryService,
                        HttpClient.newHttpClient(),
                        5000
                );

        String xml = """
            <message>
                <content>Hello Service B</content>
            </message>
            """;

        String correlationId = "test-correlation-123";

        String response = client.sendXml(xml, correlationId);

        assertTrue(
                response.contains(
                        "<content>Processed: Hello Service B</content>"
                )
        );
    }

    @Test
    void shouldReturnCorrelationIdFromServiceB() {

        RetryConfig retryConfig = new RetryConfig(3, 0);
        RetryService retryService = new RetryService(retryConfig);

        ServiceBClient client =
                new ServiceBClient("http://localhost:8081",
                        retryService,
                        HttpClient.newHttpClient(),
                        5000
                );

        String xml = """
            <message>
                <content>Hello Service B</content>
            </message>
            """;

        String correlationId = "test-correlation-123";

        ServiceBResponse response =
                client.sendXmlWithResponse(xml, correlationId);

        assertEquals(
                correlationId,
                response.correlationId()
        );
    }

    @Test
    void shouldCaptureJwtGeneratedByServiceB() {

        RetryConfig retryConfig = new RetryConfig(3, 0);
        RetryService retryService = new RetryService(retryConfig);

        ServiceBClient client =
                new ServiceBClient("http://localhost:8081",
                        retryService,
                        HttpClient.newHttpClient(),
                        5000
                );

        String xml = """
            <message>
                <content>Hello Service B</content>
            </message>
            """;

        ServiceBResponse response =
                client.sendXmlWithResponse(
                        xml,
                        "test-correlation-123"
                );

        assertNotNull(response.jwt());
        assertFalse(response.jwt().isBlank());
    }

    @Test
    void shouldSendCapturedJwtBackToServiceB() {

        RetryConfig retryConfig = new RetryConfig(3, 0);
        RetryService retryService = new RetryService(retryConfig);

        ServiceBClient client =
                new ServiceBClient("http://localhost:8081",
                        retryService,
                        HttpClient.newHttpClient(),
                        5000
                );

        String xml = """
            <message>
                <content>Hello Service B</content>
            </message>
            """;

        String correlationId = "test-correlation-123";

        ServiceBResponse firstResponse =
                client.sendXmlWithResponse(xml, correlationId);

        String jwt = firstResponse.jwt();

        ServiceBResponse secondResponse =
                client.sendXmlWithResponse(
                        xml,
                        correlationId,
                        jwt
                );

        assertEquals(jwt, secondResponse.jwt());

        assertTrue(
                secondResponse.body().contains(
                        "<content>Processed: Hello Service B</content>"
                )
        );
    }
}
