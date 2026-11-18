package com.example.service_b.service;

import com.example.service_b.exception.MessageMarshallingException;
import com.example.service_b.model.Message;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XmlMarshallingServiceTest {

    @Test
    void shouldMarshalMessageIntoXML() {
        Message message = new Message();
        message.setContent("Hello Service A");

        XmlMarshallingService service = new XmlMarshallingService();

        String xml = service.marshal(message);

        assertTrue(xml.contains("<message>"));
        assertTrue(xml.contains("<content>Hello Service A</content>"));
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
