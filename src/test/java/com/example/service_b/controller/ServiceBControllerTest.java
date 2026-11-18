package com.example.service_b.controller;

import com.example.service_b.service.XmlMarshallingService;
import com.example.service_b.service.XmlUnmarshallingService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServiceBControllerTest {

    @Test
    void shouldReturnServiceBStatus(){
        XmlUnmarshallingService unmarshallingService = new XmlUnmarshallingService();
        XmlMarshallingService marshallingService = new XmlMarshallingService();
        ServiceBController controller = new ServiceBController(unmarshallingService, marshallingService);

        String response = controller.getStatus();

        assertEquals("Service B is running", response);
    }
}
