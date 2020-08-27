package com.jlisok.youtube_activity_manager.welcomePage.services;

import com.jlisok.youtube_activity_manager.domain.utils.JwtSubjectDecoder;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class WelcomePageService {

    private final UserRepository userRepository;


    @Autowired
    public WelcomePageService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public boolean isConnectedToGoogleAccount(HttpServletRequest request) {

        return userRepository
                .findById(JwtSubjectDecoder.getUserId(request))
                .filter(u -> u.getGoogleId() != null)
                .isPresent();

    }


}
