package com.example.service_b.controller;

import org.junit.jupiter.api.Test;
import com.example.service_b.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                        .header("X-Correlation-ID", "test-correlation-123").
                        content(xml))
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
                        .header("X-Correlation-ID", "test-correlation-123")
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
                        .header("X-Correlation-ID", "test-correlation-123")
                        .content(xml))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/xml"))
                .andExpect(content().string(
                        containsString(
                                "<content>Processed: Hello Service B</content>"
                        )
                ));
    }

    @Test
    void shouldReturnCorrelationIdInResponse() throws Exception {

        String xml = """
            <message>
                <content>Hello Service B</content>
            </message>
            """;

        String correlationId = "test-correlation-123";

        mockMvc.perform(post("/api/xml")
                        .contentType("application/xml")
                        .header("X-Correlation-ID", correlationId)
                        .content(xml))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "X-Correlation-ID",
                        correlationId
                ));
    }

    @Test
    void shouldProcessRequestWhenAuthorizationHeaderIsMissing() throws Exception {

        String xml = """
            <message>
                <content>Hello Service B</content>
            </message>
            """;

        mockMvc.perform(post("/api/xml")
                        .contentType("application/xml")
                        .header("X-Correlation-ID", "test-correlation-123")
                        .content(xml))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "<content>Processed: Hello Service B</content>"
                        )
                ));
    }

    @Test
    void shouldGenerateJwtWhenAuthorizationHeaderIsMissing() throws Exception {

        String xml = """
            <message>
                <content>Hello Service B</content>
            </message>
            """;

        mockMvc.perform(post("/api/xml")
                        .contentType("application/xml")
                        .header("X-Correlation-ID", "test-correlation-123")
                        .content(xml))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"));
    }

    @Test
    void shouldProcessRequestWithValidJwt() throws Exception {

        JwtService jwtService =
                new JwtService("this-is-a-development-secret-key-12345",3600000);

        String token = jwtService.generateToken();

        String xml = """
            <message>
                <content>Hello Service B</content>
            </message>
            """;

        mockMvc.perform(post("/api/xml")
                        .contentType("application/xml")
                        .header("X-Correlation-ID", "test-correlation-123")
                        .header("Authorization", "Bearer " + token)
                        .content(xml))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Authorization",
                        "Bearer " + token
                ))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "<content>Processed: Hello Service B</content>"
                        )
                ));
    }

    @Test
    void shouldRejectRequestWithInvalidJwt() throws Exception {

        String xml = """
            <message>
                <content>Hello Service B</content>
            </message>
            """;

        mockMvc.perform(post("/api/xml")
                        .contentType("application/xml")
                        .header("X-Correlation-ID", "test-correlation-123")
                        .header("Authorization", "Bearer invalid.jwt.token")
                        .content(xml))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectRequestWithExpiredJwt() throws Exception {

        JwtService jwtService =
                new JwtService("this-is-a-development-secret-key-12345", 3600000);

        String expiredToken = jwtService.generateExpiredToken();

        String xml = """
            <message>
                <content>Hello Service B</content>
            </message>
            """;

        mockMvc.perform(post("/api/xml")
                        .contentType("application/xml")
                        .header("X-Correlation-ID", "test-correlation-123")
                        .header("Authorization", "Bearer " + expiredToken)
                        .content(xml))
                .andExpect(status().isUnauthorized());
    }
}