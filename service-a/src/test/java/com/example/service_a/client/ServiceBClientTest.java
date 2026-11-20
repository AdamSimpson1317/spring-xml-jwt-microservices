package com.example.service_a.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceBClientTest {

    @Test
    void shouldBuildServiceBXmlEndpoint() {

        ServiceBClient client =
                new ServiceBClient("http://localhost:8081");

        assertEquals(
                "http://localhost:8081/api/xml",
                client.getXmlEndpoint()
        );
    }

    @Test
    void shouldSendXmlToServiceB() {

        ServiceBClient client =
                new ServiceBClient("http://localhost:8081");

        String xml = """
            <message>
                <content>Hello Service B</content>
            </message>
            """;

        String response = client.sendXml(xml, "test-correlation-123");

        assertTrue(
                response.contains(
                        "<content>Processed: Hello Service B</content>"
                )
        );
    }

    @Test
    void shouldSendXmlWithCorrelationIdToServiceB() {

        ServiceBClient client =
                new ServiceBClient("http://localhost:8081");

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

        ServiceBClient client =
                new ServiceBClient("http://localhost:8081");

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

        ServiceBClient client =
                new ServiceBClient("http://localhost:8081");

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

        ServiceBClient client =
                new ServiceBClient("http://localhost:8081");

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
