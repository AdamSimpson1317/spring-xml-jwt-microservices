package com.example.service_a.service;

import com.example.service_a.client.ServiceBClient;
import com.example.service_a.config.RetryConfig;
import com.example.service_a.model.Message;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.net.http.HttpClient;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

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
                        HttpClient.newHttpClient(),
                        5000
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

    @Test
    void shouldMakeCorrelationIdAvailableInMdcDuringServiceBCall() {

        XmlMarshallingService marshallingService =
                new XmlMarshallingService();

        XmlUnmarshallingService unmarshallingService =
                new XmlUnmarshallingService();

        RetryConfig retryConfig =
                new RetryConfig(3, 0);

        RetryService retryService =
                new RetryService(retryConfig);

        AtomicReference<String> correlationIdDuringCall =
                new AtomicReference<>();

        ServiceBClient serviceBClient =
                new ServiceBClient(
                        "http://localhost:8081",
                        retryService,
                        HttpClient.newHttpClient(),
                        5000
                ) {
                    @Override
                    public String sendXml(
                            String xml,
                            String correlationId) {

                        correlationIdDuringCall.set(
                                MDC.get("correlationId")
                        );

                        return """
                            <message>
                                <content>Processed: Hello Service B</content>
                            </message>
                            """;
                    }
                };

        CorrelationIdService correlationIdService =
                new CorrelationIdService();

        MessageService messageService =
                new MessageService(
                        marshallingService,
                        unmarshallingService,
                        serviceBClient,
                        correlationIdService
                );

        Message message = new Message();
        message.setContent("Hello Service B");

        messageService.sendMessage(message);

        assertNotNull(correlationIdDuringCall.get());

        assertNull(MDC.get("correlationId"));
    }
}
