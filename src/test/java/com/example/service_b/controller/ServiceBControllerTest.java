package com.example.service_b.controller;

import com.example.service_b.service.XmlUnmarshallingService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServiceBControllerTest {

    @Test
    void shouldReturnServiceBStatus(){
        XmlUnmarshallingService service = new XmlUnmarshallingService();
        ServiceBController controller = new ServiceBController(service);

        String response = controller.getStatus();

        assertEquals("Service B is running", response);
    }
}
