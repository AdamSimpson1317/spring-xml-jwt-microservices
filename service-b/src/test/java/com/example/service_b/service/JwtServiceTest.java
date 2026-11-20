package com.example.service_b.service;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String TEST_SECRET =
            "this-is-a-development-secret-key-12345";

    @Test
    void shouldGenerateJwtToken() {

        JwtService jwtService = new JwtService(TEST_SECRET, 3600000);

        String token = jwtService.generateToken();

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldExtractSubjectFromGeneratedToken() {

        JwtService jwtService = new JwtService(TEST_SECRET, 3600000);

        String token = jwtService.generateToken();

        String subject = jwtService.extractSubject(token);

        assertEquals("service-b", subject);
    }

    @Test
    void shouldRejectExpiredToken() {

        JwtService jwtService =
                new JwtService("this-is-a-development-secret-key-12345", 3600000);

        String token = jwtService.generateExpiredToken();

        assertThrows(
                io.jsonwebtoken.ExpiredJwtException.class,
                () -> jwtService.extractSubject(token)
        );
    }

    @Test
    void shouldRejectTokenSignedWithDifferentSecret() {

        JwtService jwtService = new JwtService(
                "this-is-a-development-secret-key-12345",
                3600000
        );

        JwtService differentJwtService = new JwtService(
                "this-is-a-different-secret-key-67890",
                3600000
        );

        String token = differentJwtService.generateToken();

        assertThrows(
                io.jsonwebtoken.JwtException.class,
                () -> jwtService.extractSubject(token)
        );
    }

    @Test
    void shouldGenerateTokenWithFutureExpiration() {

        JwtService jwtService = new JwtService(
                "this-is-a-development-secret-key-12345",
                3600000
        );

        String token = jwtService.generateToken();

        Date expiration = jwtService.extractExpiration(token);

        assertTrue(expiration.after(new Date()));
    }
}