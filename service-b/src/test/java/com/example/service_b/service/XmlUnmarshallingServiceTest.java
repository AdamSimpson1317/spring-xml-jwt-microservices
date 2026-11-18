package com.example.service_b.service;

import com.example.service_b.model.Message;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlUnmarshallingServiceTest {

    @Test
    void shouldUnmarshalXmlIntoMessage() {

        String xml = """
                <message>
                    <content>Hello Service B</content>
                </message>
                """;

        XmlUnmarshallingService service = new XmlUnmarshallingService();

        Message message = service.unmarshal(xml);

        assertEquals("Hello Service B", message.getContent());
    }
}
