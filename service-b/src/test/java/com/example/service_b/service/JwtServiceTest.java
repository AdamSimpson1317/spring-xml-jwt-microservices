package com.example.service_b.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String TEST_SECRET =
            "this-is-a-development-secret-key-12345";

    @Test
    void shouldGenerateJwtToken() {

        JwtService jwtService = new JwtService(TEST_SECRET);

        String token = jwtService.generateToken();

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldExtractSubjectFromGeneratedToken() {

        JwtService jwtService = new JwtService(TEST_SECRET);

        String token = jwtService.generateToken();

        String subject = jwtService.extractSubject(token);

        assertEquals("service-b", subject);
    }
}