package com.example.service_a.service;

import com.example.service_a.client.ServiceBClient;
import com.example.service_a.model.Message;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final XmlMarshallingService marshallingService;
    private final XmlUnmarshallingService unmarshallingService;
    private final ServiceBClient serviceBClient;


    public MessageService(XmlMarshallingService marshallingService, XmlUnmarshallingService unmarshallingService, ServiceBClient serviceBClient) {
        this.marshallingService = marshallingService;
        this.unmarshallingService = unmarshallingService;
        this.serviceBClient = serviceBClient;
    }

    public Message sendMessage(Message message){
        String requestXml = marshallingService.marshal(message);
        String responseXml = serviceBClient.sendXml(requestXml);
        return unmarshallingService.unmarshal(responseXml);
    }
}
