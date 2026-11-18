package com.example.service_a.service;

import com.example.service_a.exception.MessageMarshallingException;
import com.example.service_a.model.Message;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XmlMarshallingServiceTest {

    @Test
    void shouldMarshalMessageIntoXml() {

        Message message = new Message();
        message.setContent("Hello Service B");

        XmlMarshallingService service = new XmlMarshallingService();

        String xml = service.marshal(message);

        assertTrue(xml.contains("<message>"));
        assertTrue(xml.contains("<content>Hello Service B</content>"));
        assertTrue(xml.contains("</message>"));
    }

    @Test
    void shouldThrowExceptionWhenMessageCannotBeMarshalled() {

        XmlMarshallingService service = new XmlMarshallingService();

        assertThrows(
                MessageMarshallingException.class,
                () -> service.marshal(null)
        );
    }
}
