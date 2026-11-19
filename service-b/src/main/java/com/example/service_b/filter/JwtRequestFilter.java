package com.example.service_b.filter;

import com.example.service_b.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtRequestFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public String getOrGenerateToken(String authorizationHeader) {

        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return jwtService.generateToken();
        }

        if (authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }

        return authorizationHeader;
    }

    public String validateToken(String token) {
        return jwtService.extractSubject(token);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authorizationHeader =
                    request.getHeader("Authorization");

            String token =
                    getOrGenerateToken(authorizationHeader);

            validateToken(token);

            response.setHeader(
                    "Authorization",
                    "Bearer " + token
            );

            filterChain.doFilter(request, response);

        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}