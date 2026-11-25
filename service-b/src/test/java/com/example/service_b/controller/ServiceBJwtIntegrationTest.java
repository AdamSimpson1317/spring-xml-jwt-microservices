package com.example.service_b.controller;

import com.example.service_b.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ServiceBJwtIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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

        JwtService expiredJwtService = new JwtService(
                "this-is-a-development-secret-key-12345",
                -1000
        );

        String xml = """
            <message>
                <content>Hello Service B</content>
            </message>
            """;

        mockMvc.perform(post("/api/xml")
                        .contentType("application/xml")
                        .header("X-Correlation-ID", "test-correlation-123")
                        .header("Authorization", "Bearer " + expiredJwtService)
                        .content(xml))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectRequestWithWrongSignature() throws Exception {

        JwtService differentJwtService = new JwtService(
                "this-is-a-different-secret-key-67890",
                3600000
        );

        String token = differentJwtService.generateToken();

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
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectUnsupportedAuthorizationScheme() throws Exception {

        String xml = """
            <message>
                <content>Hello Service B</content>
            </message>
            """;

        mockMvc.perform(post("/api/xml")
                        .contentType("application/xml")
                        .header("X-Correlation-ID", "test-correlation-123")
                        .header("Authorization", "Basic abc123")
                        .content(xml))
                .andExpect(status().isUnauthorized());
    }
}
