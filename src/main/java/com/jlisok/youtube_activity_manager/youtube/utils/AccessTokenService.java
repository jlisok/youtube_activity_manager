package com.jlisok.youtube_activity_manager.youtube.utils;

import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext.getAuthenticationInContext;

@Component
public class AccessTokenService {

    private final UserRepository repository;

    @Autowired
    public AccessTokenService(UserRepository repository) {
        this.repository = repository;
    }

    public String get() {
        UUID userId = getAuthenticationInContext().getPrincipal();
        return repository
                .findById(userId)
                .map(User::getAccessToken)
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Authentication failed. Access token not found for userId: " + userId));

    }
}
