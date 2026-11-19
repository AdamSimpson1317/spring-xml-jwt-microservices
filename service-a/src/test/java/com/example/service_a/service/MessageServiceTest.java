package com.example.service_a.service;

import com.example.service_a.client.ServiceBClient;
import com.example.service_a.model.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MessageServiceTest {

    @Test
    void shouldSendMessageToServiceBAndReturnProcessedMessage() {

        XmlMarshallingService marshallingService =
                new XmlMarshallingService();

        XmlUnmarshallingService unmarshallingService =
                new XmlUnmarshallingService();

        ServiceBClient serviceBClient =
                new ServiceBClient("http://localhost:8081");

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
