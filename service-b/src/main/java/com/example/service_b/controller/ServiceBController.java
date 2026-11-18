package com.example.service_b.controller;

import com.example.service_b.model.Message;
import com.example.service_b.service.XmlMarshallingService;
import com.example.service_b.service.XmlUnmarshallingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ServiceBController {

    private final XmlUnmarshallingService xmlUnmarshallingService;
    private final XmlMarshallingService xmlMarshallingService;

    public ServiceBController(XmlUnmarshallingService xmlUnmarshallingService, XmlMarshallingService xmlMarshallingService) {
        this.xmlUnmarshallingService = xmlUnmarshallingService;
        this.xmlMarshallingService = xmlMarshallingService;
    }

    @RequestMapping("/status")
    public String getStatus(){
        return "Service B is running";
    }

    @PostMapping(
            value = "/xml",
            consumes = "application/xml",
            produces = "application/xml"
    )
    public String receiveXml(@RequestBody String xml){
        Message message = xmlUnmarshallingService.unmarshal(xml);

        message.setContent("Processed: " + message.getContent());

        return xmlMarshallingService.marshal(message);
    }
}
