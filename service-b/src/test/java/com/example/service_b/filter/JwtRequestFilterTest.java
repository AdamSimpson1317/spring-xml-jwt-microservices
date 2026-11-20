package com.example.service_b.filter;

import com.example.service_b.service.JwtService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtRequestFilterTest {

    @Test
    void shouldGenerateTokenWhenAuthorizationHeaderIsMissing() {

        String secret = "this-is-a-development-secret-key-12345";

        JwtService jwtService = new JwtService(secret, 3600000);

        JwtRequestFilter filter = new JwtRequestFilter(jwtService);

        String token = filter.getOrGenerateToken(null);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldExtractExistingBearerToken() {

        String secret = "this-is-a-development-secret-key-12345";

        JwtService jwtService = new JwtService(secret, 3600000);

        JwtRequestFilter filter = new JwtRequestFilter(jwtService);

        String token = jwtService.generateToken();

        String authorizationHeader = "Bearer " + token;

        String result = filter.getOrGenerateToken(authorizationHeader);

        assertEquals(token, result);
    }

    @Test
    void shouldValidateExistingBearerToken() {

        String secret = "this-is-a-development-secret-key-12345";

        JwtService jwtService = new JwtService(secret, 3600000);
        JwtRequestFilter filter = new JwtRequestFilter(jwtService);

        String token = jwtService.generateToken();

        String subject = filter.validateToken(token);

        assertEquals("service-b", subject);
    }

    @Test
    void shouldRejectInvalidToken() {

        String secret = "this-is-a-development-secret-key-12345";

        JwtService jwtService = new JwtService(secret, 3600000);
        JwtRequestFilter filter = new JwtRequestFilter(jwtService);

        String invalidToken = "invalid.jwt.token";

        assertThrows(
                JwtException.class,
                () -> filter.validateToken(invalidToken)
        );
    }

    @Test
    void shouldGenerateTokenWhenAuthorizationHeaderIsBlank() {

        JwtService jwtService = new JwtService(
                "this-is-a-development-secret-key-12345",
                3600000
        );

        JwtRequestFilter filter = new JwtRequestFilter(jwtService);

        String token = filter.getOrGenerateToken("");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }
}
