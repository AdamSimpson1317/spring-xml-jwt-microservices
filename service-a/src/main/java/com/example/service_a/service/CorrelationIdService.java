package com.example.service_a.service;

import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CorrelationIdService {

    private static final String MDC_KEY = "correlationId";

    public String generate() {
        return UUID.randomUUID().toString();
    }

    public void addToMdc(String correlationId){
        MDC.put(MDC_KEY, correlationId);
    }

    public void clearFromMdc(){
        MDC.remove(MDC_KEY);
    }
}