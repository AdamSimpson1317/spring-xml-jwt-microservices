package com.example.service_a.service;

import com.example.service_a.model.Message;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XmlUnmarshallingServiceTest {

    @Test
    void shouldUnmarshalXmlIntoMessage() {

        String xml = """
                <message>
                    <content>Processed: Hello Service B</content>
                </message>
                """;

        XmlUnmarshallingService service = new XmlUnmarshallingService();

        Message message = service.unmarshal(xml);

        assertEquals(
                "Processed: Hello Service B",
                message.getContent()
        );
    }
}
