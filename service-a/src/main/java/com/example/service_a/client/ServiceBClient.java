package com.example.service_a.client;

import com.example.service_a.exception.RetryableServiceBException;
import com.example.service_a.exception.ServiceBClientException;
import com.example.service_a.service.RetryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class ServiceBClient {

    private final String serviceBUrl;
    private final RetryService retryService;
    private final HttpClient httpClient;
    private final long requestTimeoutMs;

    public ServiceBClient(@Value("${service-b.url}") String serviceBUrl,
                          RetryService retryService, HttpClient httpClient,
                          @Value("${service-b.http.request-timeout-ms}") long requestTimeoutMs) {
        this.serviceBUrl = serviceBUrl;
        this.retryService = retryService;
        this.httpClient = httpClient;
        this.requestTimeoutMs = requestTimeoutMs;
    }

    public String getXmlEndpoint(){
        return serviceBUrl + "/api/xml";
    }

    public String sendXml(String xml, String correlationId) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getXmlEndpoint()))
                .timeout(Duration.ofMillis(requestTimeoutMs))
                .header("Content-Type", "application/xml")
                .header("X-Correlation-ID", correlationId)
                .POST(HttpRequest.BodyPublishers.ofString(xml))
                .build();

        return retryService.execute(() -> {

            try {
                HttpResponse<String> response = httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

                if (response.statusCode() >= 500) {
                    throw new RetryableServiceBException(
                            "Service B returned HTTP " + response.statusCode()
                    );
                }

                if (response.statusCode() >= 400) {
                    throw new ServiceBClientException(
                            "Service B returned HTTP " + response.statusCode()
                    );
                }

                return response.body();

            } catch (IOException exception) {

                throw new RetryableServiceBException(
                        "Failed to communicate with Service B",
                        exception
                );

            } catch (InterruptedException exception) {

                Thread.currentThread().interrupt();

                throw new RuntimeException(
                        "Communication with Service B was interrupted",
                        exception
                );
            }
        });
    }

    public ServiceBResponse sendXmlWithResponse(
            String xml,
            String correlationId) {

        return sendXmlWithResponse(xml, correlationId, null);
    }

    public ServiceBResponse sendXmlWithResponse(
            String xml,
            String correlationId,
            String jwt) {

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(getXmlEndpoint()))
                .timeout(Duration.ofMillis(requestTimeoutMs))
                .header("Content-Type", "application/xml")
                .header("X-Correlation-ID", correlationId)
                .POST(HttpRequest.BodyPublishers.ofString(xml));

        if (jwt != null && !jwt.isBlank()) {
            requestBuilder.header(
                    "Authorization",
                    "Bearer " + jwt
            );
        }

        HttpRequest request = requestBuilder.build();

        return retryService.execute(() -> {

            try {
                HttpResponse<String> response = httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

                if (response.statusCode() >= 500) {
                    throw new RetryableServiceBException(
                            "Service B returned HTTP "
                                    + response.statusCode()
                    );
                }

                String returnedCorrelationId = response.headers()
                        .firstValue("X-Correlation-ID")
                        .orElse(null);

                String authorizationHeader = response.headers()
                        .firstValue("Authorization")
                        .orElse(null);

                String returnedJwt = null;

                if (authorizationHeader != null
                        && authorizationHeader.startsWith("Bearer ")) {

                    returnedJwt = authorizationHeader.substring(7);
                }

                return new ServiceBResponse(
                        response.body(),
                        returnedCorrelationId,
                        returnedJwt
                );

            } catch (IOException exception) {

                throw new RetryableServiceBException(
                        "Failed to communicate with Service B",
                        exception
                );

            } catch (InterruptedException exception) {

                Thread.currentThread().interrupt();

                throw new RuntimeException(
                        "Communication with Service B was interrupted",
                        exception
                );
            }
        });
    }
}
