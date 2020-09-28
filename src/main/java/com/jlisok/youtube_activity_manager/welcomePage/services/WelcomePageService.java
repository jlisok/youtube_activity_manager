package com.jlisok.youtube_activity_manager.welcomePage.services;

import com.jlisok.youtube_activity_manager.security.configs.JwtAuthentication;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext.getAuthenticationInContext;

@Service
public class WelcomePageService {

    private final UserRepository userRepository;


    @Autowired
    public WelcomePageService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public boolean isConnectedToGoogleAccount() {
        JwtAuthentication authentication = getAuthenticationInContext();
        return userRepository
                .findById(authentication.getPrincipal())
                .filter(u -> u.getGoogleId() != null)
                .isPresent();
    }
}
