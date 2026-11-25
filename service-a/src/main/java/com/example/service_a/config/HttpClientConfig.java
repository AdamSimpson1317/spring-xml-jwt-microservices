package com.example.service_a.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class HttpClientConfig {

    private final long connectTimeoutMs;

    public HttpClientConfig(
            @Value("${service-b.http.connect-timeout-ms}") long connectTimeoutMs) {

        this.connectTimeoutMs = connectTimeoutMs;
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

}
