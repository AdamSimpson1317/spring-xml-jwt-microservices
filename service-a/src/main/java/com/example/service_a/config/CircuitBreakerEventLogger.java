package com.example.service_a.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CircuitBreakerEventLogger {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakerEventLogger(
            CircuitBreakerRegistry circuitBreakerRegistry) {

        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @PostConstruct
    public void registerEventListener() {

        circuitBreakerRegistry
                .circuitBreaker("serviceB")
                .getEventPublisher()
                .onStateTransition(event ->
                        log.warn(
                                "Circuit breaker '{}' state changed from {} to {}",
                                event.getCircuitBreakerName(),
                                event.getStateTransition().getFromState(),
                                event.getStateTransition().getToState()
                        )
                );
    }
}