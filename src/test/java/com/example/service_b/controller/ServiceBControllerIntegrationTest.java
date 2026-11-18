package com.example.service_b.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ServiceBControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnServiceBStatus() throws Exception {
        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("Service B is running"));
    }

    @Test
    void shouldReceiveXMLRequest() throws Exception {
        String xml = """
                <message>
                    <content>Hello Service B</content>
                </message>
                """;

        mockMvc.perform(post("/api/xml")
                        .contentType("application/xml")
                        .content(xml))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/xml"))
                .andExpect(content().string(
                        containsString("<content>Processed: Hello Service B</content")));
    }

    @Test
    void shouldReturnBadRequestForMalformedXml() throws Exception {

        String invalidXml = """
            <message>
                <content>Hello Service B</content>
            """;

        mockMvc.perform(post("/api/xml")
                        .contentType("application/xml")
                        .content(invalidXml))
                .andExpect(status().isBadRequest());
    }
    @Test
    void shouldReturnBadRequestWhenContentIsMissing() throws Exception {

        String xml = """
            <message>
            <message>
            """;

        mockMvc.perform(post("/api/xml")
                        .contentType("application/xml")
                        .content(xml))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnMarshalledXmlResponse() throws Exception {

        String xml = """
            <message>
                <content>Hello Service B</content>
            </message>
            """;

        mockMvc.perform(post("/api/xml")
                        .contentType("application/xml")
                        .content(xml))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/xml"))
                .andExpect(content().string(
                        containsString(
                                "<content>Processed: Hello Service B</content>"
                        )
                ));
    }
}