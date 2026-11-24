package com.example.service_a.service;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

class CorrelationIdServiceTest {

    @Test
    void shouldGenerateCorrelationId() {

        CorrelationIdService service = new CorrelationIdService();

        String correlationId = service.generate();

        assertNotNull(correlationId);
        assertFalse(correlationId.isBlank());
    }

    @Test
    void shouldAddGeneratedCorrelationIdToMdc() {

        CorrelationIdService service = new CorrelationIdService();

        String correlationId = service.generate();

        service.addToMdc(correlationId);

        assertEquals(
                correlationId,
                MDC.get("correlationId")
        );
    }
}
