package com.jlisok.youtube_activity_manager.security.configs;


import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;
import java.util.UUID;

public class JwtAuthentication extends AbstractAuthenticationToken {

    private final String token;
    private final UUID userId;

    public JwtAuthentication(String token, UUID userId) {
        super(Collections.emptyList());
        this.token = token;
        this.userId = userId;
    }

    @Override
    public String getCredentials() {
        return token;
    }

    @Override
    public UUID getPrincipal() {
        return userId;
    }

    public UUID getAuthenticatedUserId() {
        return userId;
    }
}
