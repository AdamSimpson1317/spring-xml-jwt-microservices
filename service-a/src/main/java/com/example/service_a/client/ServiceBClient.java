package com.example.service_a.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class ServiceBClient {

    private final String serviceBUrl;

    public ServiceBClient(@Value("${service-b.url}") String serviceBUrl) {
        this.serviceBUrl = serviceBUrl;
    }

    public String getXmlEndpoint(){
        return serviceBUrl + "/api/xml";
    }

    public String sendXml(String xml, String correlationId) {

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getXmlEndpoint()))
                .header("Content-Type", "application/xml")
                .header("X-Correlation-ID", correlationId)
                .POST(HttpRequest.BodyPublishers.ofString(xml))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            return response.body();

        } catch (IOException | InterruptedException exception) {
            throw new RuntimeException(
                    "Failed to communicate with Service B",
                    exception
            );
        }
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

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(getXmlEndpoint()))
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

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

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

        } catch (IOException | InterruptedException exception) {
            throw new RuntimeException(
                    "Failed to communicate with Service B",
                    exception
            );
        }
    }
}
