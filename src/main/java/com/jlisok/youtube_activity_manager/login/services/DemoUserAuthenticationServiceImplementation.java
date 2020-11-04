package com.jlisok.youtube_activity_manager.login.services;

import com.jlisok.youtube_activity_manager.login.exceptions.DemoUserNotFound;
import com.jlisok.youtube_activity_manager.login.utils.TokenCreator;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class DemoUserAuthenticationServiceImplementation implements DemoUserAuthenticationService {

    @Value("${demo.user_email}")
    private String demoUserEmail;
    private final UserRepository userRepository;
    private final TokenCreator tokenCreator;

    @Autowired
    public DemoUserAuthenticationServiceImplementation(UserRepository userRepository, TokenCreator tokenCreator) {
        this.userRepository = userRepository;
        this.tokenCreator = tokenCreator;
    }

    @Override
    public String authenticateUser() {
        User demoUser = userRepository
                .findByEmail(demoUserEmail)
                .orElseThrow(() -> new DemoUserNotFound("Authentication failed. Demo user " + demoUserEmail + " not found."));
        return tokenCreator.create(demoUser.getId().toString(), Instant.now(), demoUser.checkIfEverAuthorized());
    }
}
