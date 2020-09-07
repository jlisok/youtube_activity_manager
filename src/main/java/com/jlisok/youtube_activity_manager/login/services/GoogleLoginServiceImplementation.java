package com.jlisok.youtube_activity_manager.login.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.jlisok.youtube_activity_manager.login.dto.GoogleLoginRequestDto;
import com.jlisok.youtube_activity_manager.login.exceptions.DataInconsistencyAuthenticationException;
import com.jlisok.youtube_activity_manager.login.exceptions.EmailNotVerifiedAuthenticationException;
import com.jlisok.youtube_activity_manager.login.utils.TokenCreator;
import com.jlisok.youtube_activity_manager.userPersonalData.models.UserPersonalData;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.UUID;

@Service
public class GoogleLoginServiceImplementation implements GoogleLoginService {

    private final UserRepository userRepository;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final TokenCreator tokenCreator;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public GoogleLoginServiceImplementation(UserRepository userRepository, GoogleIdTokenVerifier googleIdTokenVerifier, TokenCreator tokenCreator) {
        logger.debug("GoogleLoginService - initialization.");
        this.userRepository = userRepository;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
        this.tokenCreator = tokenCreator;
    }


    @Override
    public String authenticateUser(GoogleLoginRequestDto loginRequestDto) throws GeneralSecurityException, IOException, AuthenticationException {
        GoogleIdToken googleIdToken = verifyGoogleIdToken(loginRequestDto.getToken());
        User user = verifyIfUserInDatabaseOrCreateNewUser(loginRequestDto.getToken(), googleIdToken.getPayload());
        return createTokenIfAuthorized(user.getId());
    }


    private GoogleIdToken verifyGoogleIdToken(String googleToken) throws GeneralSecurityException, IOException {
        GoogleIdToken idToken = googleIdTokenVerifier.verify(googleToken);
        if (idToken != null) {
            logger.debug("GoogleLoginService - GoogleIdToken verified and valid.");
            return idToken;
        } else {
            throw new AuthenticationCredentialsNotFoundException("Validation of Google token failed. " + googleToken + " did not pass verifier.");
        }
    }


    private User verifyIfUserInDatabaseOrCreateNewUser(String googleIdToken, Payload userData) {
        Instant now = Instant.now();
        UUID googleId = UUID.fromString(userData.getSubject());
        return userRepository
                .findByEmail(userData.getEmail())
                .map(user -> updateGoogleDataInDatabase(user, googleId, googleIdToken, now))
                .orElseGet(() -> createNewUserInDatabase(googleIdToken, googleId, userData, now));
    }


    private User updateGoogleDataInDatabase(User user, UUID googleId, String token, Instant now) throws DataInconsistencyAuthenticationException {
        if (userRepository.findByGoogleId(googleId).isPresent()) {
            throw new DataInconsistencyAuthenticationException("Updating user: " + user.getEmail() + " failed. Given googleId " + user.getGoogleId() + " already exists in database under different email.");
        }
        if (user.getGoogleId() == null) {
            user.setGoogleId(googleId);
        }
        user.setGoogleIdToken(token);
        user.setModifiedAt(now);
        User updatedUser = userRepository.saveAndFlush(user);
        logger.debug("GoogleLoginService - updating googleIdToken - success");
        return updatedUser;
    }


    private User createNewUserInDatabase(String googleIdToken, UUID googleId, Payload userData, Instant now) throws EmailNotVerifiedAuthenticationException {
        if (userData.getEmailVerified()) {
            UUID userId = UUID.randomUUID();
            String fistName = userData.get("given_name").toString();
            UserPersonalData userPersonalData = new UserPersonalData(userId, fistName, now, now);
            User userToSave = new User(userId, userData.getEmail(), googleId, googleIdToken, now, now, userPersonalData);
            User savedUser = userRepository.saveAndFlush(userToSave);
            logger.debug("GoogleLoginService - creating new User in database - success");
            return savedUser;
        } else {
            throw new EmailNotVerifiedAuthenticationException("Google registration failed. " + userData.getEmail() + " was not verified by Google.");
        }
    }


    private String createTokenIfAuthorized(UUID userId) {
        Instant now = Instant.now();
        String token = tokenCreator.create(userId.toString(), now);
        logger.info("GoogleLoginService - success.");
        return token;
    }
}
