package com.example.service_a.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CorrelationIdServiceTest {

    @Test
    void shouldGenerateCorrelationId() {

        CorrelationIdService service = new CorrelationIdService();

        String correlationId = service.generate();

        assertNotNull(correlationId);
        assertFalse(correlationId.isBlank());
    }
}
