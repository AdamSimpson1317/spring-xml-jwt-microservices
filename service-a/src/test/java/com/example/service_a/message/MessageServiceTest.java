package com.example.service_a.message;

import com.example.service_a.client.ServiceBClient;
import com.example.service_a.model.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.service_a.service.MessageService;
import com.example.service_a.service.XmlMarshallingService;
import com.example.service_a.service.XmlUnmarshallingService;
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

        MessageService messageService = new MessageService(
                marshallingService,
                unmarshallingService,
                serviceBClient
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
