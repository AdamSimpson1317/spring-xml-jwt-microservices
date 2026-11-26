package com.example.service_a.client;

import com.example.service_a.exception.RetryExhaustedException;
import com.sun.net.httpserver.HttpServer;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ServiceBClientCircuitBreakerTest {

    private static HttpServer server;

    private static final AtomicInteger attempts =
            new AtomicInteger();

    private static final AtomicBoolean serviceBHealthy =
            new AtomicBoolean(false);

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private ServiceBClient client;

    @BeforeEach
    void resetCircuitBreaker() {

        attempts.set(0);
        serviceBHealthy.set(false);

        circuitBreakerRegistry
                .circuitBreaker("serviceB")
                .reset();
    }

    @BeforeAll
    static void startServer() throws Exception {

        server = HttpServer.create(
                new InetSocketAddress(0),
                0
        );

        server.createContext("/api/xml", exchange -> {

            attempts.incrementAndGet();

            if (!serviceBHealthy.get()) {

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
    }

    @AfterAll
    static void stopServer() {

        if (server != null) {
            server.stop(0);
        }
    }

    @DynamicPropertySource
    static void configureProperties(
            DynamicPropertyRegistry registry) {

        registry.add(
                "service-b.url",
                () -> "http://localhost:"
                        + server.getAddress().getPort()
        );

        registry.add(
                "service-b.retry.max-attempts",
                () -> 3
        );

        registry.add(
                "service-b.retry.delay-ms",
                () -> 0
        );
    }

    @Test
    void shouldStopCallingServiceBAfterRepeatedFailures() {

        String xml = """
                <message>
                    <content>Hello Service B</content>
                </message>
                """;

        assertThrows(
                RetryExhaustedException.class,
                () -> client.sendXml(
                        xml,
                        "test-correlation-1"
                )
        );

        assertThrows(
                RetryExhaustedException.class,
                () -> client.sendXml(
                        xml,
                        "test-correlation-2"
                )
        );

        assertThrows(
                CallNotPermittedException.class,
                () -> client.sendXml(
                        xml,
                        "test-correlation-3"
                )
        );

        assertEquals(6, attempts.get());
    }

    @Test
    void shouldCloseCircuitWhenServiceBRecovers() {

        String xml = """
            <message>
                <content>Hello Service B</content>
            </message>
            """;

        assertThrows(
                RetryExhaustedException.class,
                () -> client.sendXml(
                        xml,
                        "test-correlation-1"
                )
        );

        assertThrows(
                RetryExhaustedException.class,
                () -> client.sendXml(
                        xml,
                        "test-correlation-2"
                )
        );

        CircuitBreaker circuitBreaker =
                circuitBreakerRegistry.circuitBreaker("serviceB");

        assertEquals(
                CircuitBreaker.State.OPEN,
                circuitBreaker.getState()
        );

        serviceBHealthy.set(true);

        circuitBreaker.transitionToHalfOpenState();

        String response = client.sendXml(
                xml,
                "test-correlation-recovery"
        );

        assertEquals(
                CircuitBreaker.State.CLOSED,
                circuitBreaker.getState()
        );

        assertTrue(
                response.contains("Processed: Hello Service B")
        );

        assertEquals(7, attempts.get());
    }
}