package com.jlisok.youtube_activity_manager.users.utils;

import com.jlisok.youtube_activity_manager.login.exceptions.DataInconsistencyAuthenticationException;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UserUtils {

    private final UserRepository userRepository;

    @Autowired
    public UserUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User updateGoogleDataInDatabase(User user, String googleId, String token, String accessToken, Instant now) {
        if (user.getGoogleId() == null) {
            user.setGoogleId(googleId);
        }
        user.setGoogleIdToken(token);
        user.setAccessToken(accessToken);
        user.setModifiedAt(now);
        try {
            return userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw new DataInconsistencyAuthenticationException("Updating user: " + user.getEmail() + " failed. Given googleId " + user
                    .getGoogleId() + " already exists in database under different email.", e);
        }
    }
}
