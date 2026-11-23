package com.example.service_a.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class HttpClientConfig {

    private final long connectTimeoutMs;
    private final long requestTimeoutMs;

    public HttpClientConfig(
            @Value("${service-b.http.connect-timeout-ms}") long connectTimeoutMs,
            @Value("${service-b.http.request-timeout-ms}") long requestTimeoutMs) {

        this.connectTimeoutMs = connectTimeoutMs;
        this.requestTimeoutMs = requestTimeoutMs;
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeoutMs))
                .build();
    }

    public long getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public long getRequestTimeoutMs() {
        return requestTimeoutMs;
    }
}
