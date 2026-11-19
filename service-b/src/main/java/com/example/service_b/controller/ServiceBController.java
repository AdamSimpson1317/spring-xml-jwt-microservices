package com.example.service_b.controller;

import com.example.service_b.model.Message;
import com.example.service_b.service.XmlMarshallingService;
import com.example.service_b.service.XmlUnmarshallingService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> receiveXml(
            @RequestBody String xml, @RequestHeader("X-Correlation-ID") String correlationId){
        Message message = xmlUnmarshallingService.unmarshal(xml);

        message.setContent("Processed: " + message.getContent());

        String responseXml = xmlMarshallingService.marshal(message);

        return ResponseEntity.ok()
                .header("X-Correlation-Id", correlationId)
                .contentType(MediaType.APPLICATION_XML)
                .body(responseXml);
    }
}
