package com.example.service_a.service;

import com.example.service_a.client.ServiceBClient;
import com.example.service_a.model.Message;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final XmlMarshallingService marshallingService;
    private final XmlUnmarshallingService unmarshallingService;
    private final ServiceBClient serviceBClient;
    private final CorrelationIdService correlationIdService;


    public MessageService(XmlMarshallingService marshallingService, XmlUnmarshallingService unmarshallingService, ServiceBClient serviceBClient, CorrelationIdService correlationIdService) {
        this.marshallingService = marshallingService;
        this.unmarshallingService = unmarshallingService;
        this.serviceBClient = serviceBClient;
        this.correlationIdService = correlationIdService;
    }

    public Message sendMessage(Message message){
        String requestXml = marshallingService.marshal(message);
        String correlationId = correlationIdService.generate();
        String responseXml =
                serviceBClient.sendXml(requestXml, correlationId);
        return unmarshallingService.unmarshal(responseXml);
    }
}
