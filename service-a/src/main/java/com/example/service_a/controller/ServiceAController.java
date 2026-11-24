package com.example.service_a.controller;

import com.example.service_a.model.Message;
import com.example.service_a.service.MessageService;
import com.example.service_a.service.XmlUnmarshallingService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ServiceAController {

    private final MessageService messageService;
    private final XmlUnmarshallingService xmlUnmarshallingService;

    public ServiceAController(MessageService messageService, XmlUnmarshallingService xmlUnmarshallingService) {
        this.messageService = messageService;
        this.xmlUnmarshallingService = xmlUnmarshallingService;
    }

    @PostMapping(
            value = "/message",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public Message sendMessage(@RequestBody String xml){
        Message message = xmlUnmarshallingService.unmarshal(xml);
        return messageService.sendMessage(message);
    }
}
