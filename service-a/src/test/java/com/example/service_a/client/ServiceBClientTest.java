package com.example.service_a.client;

import com.example.service_a.config.HttpClientConfig;
import com.example.service_a.config.RetryConfig;
import com.example.service_a.exception.RetryExhaustedException;
import com.example.service_a.exception.ServiceBClientException;
import com.example.service_a.service.RetryService;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Test
    void shouldSendXmlToServiceB() {

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

        String response = client.sendXml(xml, "test-correlation-123");

        assertTrue(
                response.contains(
                        "<content>Processed: Hello Service B</content>"
                )
        );
    }

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

    @Test
    void shouldThrowRetryableExceptionWhenServiceBIsUnavailable() {

        RetryConfig retryConfig = new RetryConfig(3, 0);
        RetryService retryService = new RetryService(retryConfig);

        ServiceBClient client =
                new ServiceBClient("http://localhost:9999",
                        retryService,
                        HttpClient.newHttpClient(),
                        5000
                );

        String xml = """
            <message>
                <content>Hello Service B</content>
            </message>
            """;

        assertThrows(
                RetryExhaustedException.class,
                () -> client.sendXml(
                        xml,
                        "test-correlation-123"
                )
        );
    }

    @Test
    void shouldThrowRetryableExceptionWhenServiceBIsUnavailableForResponse() {

        RetryConfig retryConfig = new RetryConfig(3, 0);
        RetryService retryService = new RetryService(retryConfig);

        ServiceBClient client =
                new ServiceBClient("http://localhost:9999",
                        retryService,
                        HttpClient.newHttpClient(),
                        5000
                );

        String xml = """
            <message>
                <content>Hello Service B</content>
            </message>
            """;

        assertThrows(
                RetryExhaustedException.class,
                () -> client.sendXmlWithResponse(
                        xml,
                        "test-correlation-123"
                )
        );
    }

    @Test
    void shouldCreateClientWithRetryService() {

        RetryConfig retryConfig = new RetryConfig(3, 0);
        RetryService retryService = new RetryService(retryConfig);

        ServiceBClient client = new ServiceBClient(
                "http://localhost:8081",
                retryService,
                HttpClient.newHttpClient(),
                5000
        );

        assertNotNull(client);
    }

    @Test
    void shouldCreateClientWithInjectedHttpClient() {

        RetryConfig retryConfig = new RetryConfig(3, 0);
        RetryService retryService = new RetryService(retryConfig);

        HttpClient httpClient = HttpClient.newHttpClient();

        ServiceBClient client = new ServiceBClient(
                "http://localhost:8081",
                retryService,
                httpClient,
                5000
        );

        assertNotNull(client);
    }

    @Test
    void shouldRetryServiceBCallUntilItSucceeds() throws Exception {

        AtomicInteger attempts = new AtomicInteger();

        HttpServer server = HttpServer.create(
                new InetSocketAddress(0),
                0
        );

        server.createContext("/api/xml", exchange -> {

            int attempt = attempts.incrementAndGet();

            if (attempt < 3) {

                exchange.sendResponseHeaders(503, -1);
                exchange.close();
                return;
            }

            String responseXml = """
                <message>
                    <content>Processed: Hello Service B</content>
                </message>
                """;

            byte[] responseBytes =
                    responseXml.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders()
                    .add("Content-Type", "application/xml");

            exchange.sendResponseHeaders(
                    200,
                    responseBytes.length
            );

            exchange.getResponseBody().write(responseBytes);
            exchange.close();
        });

        server.start();

        try {
            int port = server.getAddress().getPort();

            RetryConfig retryConfig =
                    new RetryConfig(3, 0);

            RetryService retryService =
                    new RetryService(retryConfig);

            ServiceBClient client =
                    new ServiceBClient(
                            "http://localhost:" + port,
                            retryService,
                            HttpClient.newHttpClient(),
                            5000
                    );

            String xml = """
                <message>
                    <content>Hello Service B</content>
                </message>
                """;

            String response = client.sendXml(
                    xml,
                    "test-correlation-123"
            );

            assertTrue(
                    response.contains(
                            "Processed: Hello Service B"
                    )
            );

            assertEquals(3, attempts.get());

        } finally {
            server.stop(0);
        }
    }

    @Test
    void shouldRetryServiceBCallWithResponseUntilItSucceeds()
            throws Exception {

        AtomicInteger attempts = new AtomicInteger();

        HttpServer server = HttpServer.create(
                new InetSocketAddress(0),
                0
        );

        server.createContext("/api/xml", exchange -> {

            int attempt = attempts.incrementAndGet();

            if (attempt < 3) {
                exchange.sendResponseHeaders(503, -1);
                exchange.close();
                return;
            }

            String responseXml = """
                    <message>
                        <content>Processed: Hello Service B</content>
                    </message>
                    """;

            byte[] responseBytes =
                    responseXml.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders()
                    .add(
                            "Content-Type",
                            "application/xml"
                    );

            exchange.getResponseHeaders()
                    .add(
                            "X-Correlation-ID",
                            "test-correlation-123"
                    );

            exchange.getResponseHeaders()
                    .add(
                            "Authorization",
                            "Bearer test-jwt-token"
                    );

            exchange.sendResponseHeaders(
                    200,
                    responseBytes.length
            );

            exchange.getResponseBody()
                    .write(responseBytes);

            exchange.close();
        });

        server.start();

        try {
            int port = server.getAddress().getPort();

            RetryService retryService =
                    new RetryService(new RetryConfig(3, 0));

            ServiceBClient client = new ServiceBClient(
                    "http://localhost:" + port,
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

            assertTrue(
                    response.body().contains(
                            "Processed: Hello Service B"
                    )
            );

            assertEquals(
                    "test-correlation-123",
                    response.correlationId()
            );

            assertEquals(
                    "test-jwt-token",
                    response.jwt()
            );

            assertEquals(
                    3,
                    attempts.get()
            );

        } finally {
            server.stop(0);
        }
    }

    @Test
    void shouldNotRetryBadRequest() throws Exception {

        AtomicInteger attempts = new AtomicInteger();

        HttpServer server = HttpServer.create(
                new InetSocketAddress(0),
                0
        );

        server.createContext("/api/xml", exchange -> {
            attempts.incrementAndGet();
            exchange.sendResponseHeaders(400, -1);
            exchange.close();
        });

        server.start();

        try {
            int port = server.getAddress().getPort();

            RetryService retryService =
                    new RetryService(new RetryConfig(3, 0));

            ServiceBClient client = new ServiceBClient(
                    "http://localhost:" + port,
                    retryService,
                    HttpClient.newHttpClient(),
                    5000
            );

            String xml = """
                <message>
                    <content>Hello Service B</content>
                </message>
                """;

            assertThrows(
                    ServiceBClientException.class,
                    () -> client.sendXml(
                            xml,
                            "test-correlation-123"
                    )
            );

            assertEquals(1, attempts.get());

        } finally {
            server.stop(0);
        }
    }

    @Test
    void shouldNotRetryUnauthorizedRequest() throws Exception {

        AtomicInteger attempts = new AtomicInteger();

        HttpServer server = HttpServer.create(
                new InetSocketAddress(0),
                0
        );

        server.createContext("/api/xml", exchange -> {
            attempts.incrementAndGet();
            exchange.sendResponseHeaders(401, -1);
            exchange.close();
        });

        server.start();

        try {
            int port = server.getAddress().getPort();

            RetryService retryService =
                    new RetryService(new RetryConfig(3, 0));

            ServiceBClient client = new ServiceBClient(
                    "http://localhost:" + port,
                    retryService,
                    HttpClient.newHttpClient(),
                    5000
            );

            String xml = """
                <message>
                    <content>Hello Service B</content>
                </message>
                """;

            assertThrows(
                    ServiceBClientException.class,
                    () -> client.sendXml(
                            xml,
                            "test-correlation-123"
                    )
            );

            assertEquals(1, attempts.get());

        } finally {
            server.stop(0);
        }
    }

    @Test
    void shouldRetryWhenServiceBRequestTimesOut() throws Exception {

        AtomicInteger attempts = new AtomicInteger();

        HttpServer server = HttpServer.create(
                new InetSocketAddress(0),
                0
        );

        server.setExecutor(
                java.util.concurrent.Executors.newCachedThreadPool()
        );

        server.createContext("/api/xml", exchange -> {

            attempts.incrementAndGet();

            try {
                Thread.sleep(200);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }

            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });

        server.start();

        try {
            int port = server.getAddress().getPort();

            RetryService retryService =
                    new RetryService(new RetryConfig(3, 0));

            HttpClientConfig httpClientConfig =
                    new HttpClientConfig(1000, 50);

            ServiceBClient client = new ServiceBClient(
                    "http://localhost:" + port,
                    retryService,
                    httpClientConfig.httpClient(),
                    50
            );

            String xml = """
                <message>
                    <content>Hello Service B</content>
                </message>
                """;

            assertThrows(
                    RetryExhaustedException.class,
                    () -> client.sendXml(
                            xml,
                            "test-correlation-123"
                    )
            );

            assertEquals(3, attempts.get());

        } finally {
            server.stop(0);
        }
    }
}
