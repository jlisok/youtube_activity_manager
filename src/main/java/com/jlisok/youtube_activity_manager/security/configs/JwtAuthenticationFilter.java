package com.jlisok.youtube_activity_manager.security.configs;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext.setAuthenticationInContext;
import static com.jlisok.youtube_activity_manager.security.configs.JwtSecurityConstants.HEADER_START_SCHEMA;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTVerifier jwtVerifier;

    @Autowired
    public JwtAuthenticationFilter(JWTVerifier jwtVerifier) {
        this.jwtVerifier = jwtVerifier;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String authHeader = request.getHeader(AUTHORIZATION);

        if (authHeader != null) {
            try {
                String token = authHeader.substring(HEADER_START_SCHEMA.length());
                DecodedJWT decodedToken = jwtVerifier.verify(token);
                UUID userId = UUID.fromString(decodedToken.getSubject());
                setAuthenticationInContext(token, userId);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
