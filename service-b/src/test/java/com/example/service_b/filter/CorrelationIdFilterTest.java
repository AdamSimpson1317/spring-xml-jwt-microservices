package com.example.service_b.filter;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CorrelationIdFilterTest {

    @AfterEach
    void cleanUp(){
        MDC.clear();
    }

    @Test
    void shouldAddCorrelationIdToMdc(){

        CorrelationIdFilter filter = new CorrelationIdFilter();

        filter.setCorrelationId("test-correlation-123");

        assertEquals(
                "test-correlation-123",
                MDC.get("correlationId")
        );
    }

    @Test
    void shouldClearCorrelationIdFromMdc() {

        CorrelationIdFilter filter = new CorrelationIdFilter();

        filter.setCorrelationId("test-correlation-123");

        filter.clearCorrelationId();

        assertNull(MDC.get("correlationId"));
    }

    @Test
    void shouldClearCorrelationIdAfterRequestCompletes() throws Exception {

        CorrelationIdFilter filter = new CorrelationIdFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(
                "X-Correlation-ID",
                "test-correlation-123"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(MDC.get("correlationId"));
    }

    @Test
    void shouldMakeCorrelationIdAvailableDuringRequest() throws Exception {

        CorrelationIdFilter filter = new CorrelationIdFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(
                "X-Correlation-ID",
                "test-correlation-123"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        AtomicReference<String> correlationIdDuringRequest =
                new AtomicReference<>();

        FilterChain filterChain = (req, res) ->
                correlationIdDuringRequest.set(
                        MDC.get("correlationId")
                );

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertEquals(
                "test-correlation-123",
                correlationIdDuringRequest.get()
        );

        assertNull(MDC.get("correlationId"));
    }
}
