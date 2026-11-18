package com.example.service_a.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        String response = client.sendXml(xml);

        assertTrue(
                response.contains(
                        "<content>Processed: Hello Service B</content>"
                )
        );
    }
}
