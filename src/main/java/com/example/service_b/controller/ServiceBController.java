package com.example.service_b.controller;

import com.example.service_b.model.Message;
import com.example.service_b.service.XmlUnmarshallingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ServiceBController {

    private final XmlUnmarshallingService xmlUnmarshallingService;

    public ServiceBController(XmlUnmarshallingService xmlUnmarshallingService) {
        this.xmlUnmarshallingService = xmlUnmarshallingService;
    }

    @RequestMapping("/status")
    public String getStatus(){
        return "Service B is running";
    }

    @PostMapping(
            value = "/xml",
            consumes = "application/xml"
    )
    public String receiveXml(@RequestBody String xml){
        Message message = xmlUnmarshallingService.unmarshal(xml);

        return "Received: " + message.getContent();
    }
}
