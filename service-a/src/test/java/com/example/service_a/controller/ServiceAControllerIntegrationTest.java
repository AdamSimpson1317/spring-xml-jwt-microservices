package com.example.service_a.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class ServiceAControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAcceptMessageRequest() throws Exception {

        String xml = """
                <message>
                    <content>Hello Service B</content>
                </message>
                """;

        mockMvc.perform(post("/api/message")
                        .contentType("application/xml")
                        .content(xml))
                .andExpect(content().contentTypeCompatibleWith("application/xml"))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "<content>Processed: Hello Service B</content>"
                        )
                ));
    }
}
