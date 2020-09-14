package com.jlisok.youtube_activity_manager.security.configs;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class JwtAuthenticationContext {

    public static void setAuthenticationInContext(String token, UUID userId) {
        JwtAuthentication jwtAuthentication = new JwtAuthentication(token, userId);
        jwtAuthentication.setAuthenticated(true);
        SecurityContextHolder
                .getContext()
                .setAuthentication(jwtAuthentication);
    }

    public static JwtAuthentication getAuthenticationInContext() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        String token = (String) authentication.getCredentials();
        UUID userId = (UUID) authentication.getPrincipal();
        return new JwtAuthentication(token, userId);
    }
}
