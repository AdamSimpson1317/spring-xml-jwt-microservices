package com.example.service_b.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

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
                .andExpect(content().string("Received: Hello Service B"));
    }
}