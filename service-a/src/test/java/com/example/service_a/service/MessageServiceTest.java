package com.example.service_a.service;

import com.example.service_a.client.ServiceBClient;
import com.example.service_a.config.RetryConfig;
import com.example.service_a.model.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

class MessageServiceTest {

    @Test
    void shouldSendMessageToServiceBAndReturnProcessedMessage() {

        RetryConfig retryConfig = new RetryConfig(3, 0);
        RetryService retryService = new RetryService(retryConfig);

        XmlMarshallingService marshallingService =
                new XmlMarshallingService();

        XmlUnmarshallingService unmarshallingService =
                new XmlUnmarshallingService();

        ServiceBClient serviceBClient =
                new ServiceBClient("http://localhost:8081",
                        retryService,
                        HttpClient.newHttpClient()
                );

        CorrelationIdService correlationIdService =
                new CorrelationIdService();

        MessageService messageService = new MessageService(
                marshallingService,
                unmarshallingService,
                serviceBClient,
                correlationIdService
        );

        Message message = new Message();
        message.setContent("Hello Service B");

        Message response = messageService.sendMessage(message);

        assertEquals(
                "Processed: Hello Service B",
                response.getContent()
        );
    }
}
