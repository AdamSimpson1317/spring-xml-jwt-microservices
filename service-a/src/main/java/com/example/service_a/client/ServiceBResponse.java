package com.example.service_a.client;

public record ServiceBResponse(
        String body,
        String correlationId
) {
}
