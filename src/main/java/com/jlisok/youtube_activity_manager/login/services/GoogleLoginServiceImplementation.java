package com.jlisok.youtube_activity_manager.login.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.jlisok.youtube_activity_manager.login.dto.AuthenticationDto;
import com.jlisok.youtube_activity_manager.login.dto.GoogleLoginRequestDto;
import com.jlisok.youtube_activity_manager.login.exceptions.DataInconsistencyAuthenticationException;
import com.jlisok.youtube_activity_manager.login.exceptions.EmailNotVerifiedAuthenticationException;
import com.jlisok.youtube_activity_manager.login.utils.TokenCreator;
import com.jlisok.youtube_activity_manager.userPersonalData.models.UserPersonalData;
import com.jlisok.youtube_activity_manager.userPersonalData.models.UserPersonalDataBuilder;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.models.UserBuilder;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Optional;
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
    @Transactional
    public AuthenticationDto authenticateUser(GoogleLoginRequestDto loginRequestDto) throws GeneralSecurityException, IOException, AuthenticationException {
        String stringGoogleIdToken = loginRequestDto.getGoogleIdToken();
        GoogleIdToken googleIdToken = verifyGoogleIdToken(stringGoogleIdToken);
        User user = verifyIfUserInDatabaseOrCreateNewUser(stringGoogleIdToken, loginRequestDto.getAccessToken(), googleIdToken
                .getPayload());
        var jwtToken = createTokenIfAuthorized(user.getId(), user.checkIfEverAuthorized());
        return new AuthenticationDto(user.getId(), jwtToken);
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

    private User verifyIfUserInDatabaseOrCreateNewUser(String googleIdToken, String accessToken, Payload userData) {
        Instant now = Instant.now();
        String googleId = userData.getSubject();
        return userRepository
                .findByEmail(userData.getEmail())
                .map(user -> updateGoogleDataInDatabase(user, googleId, googleIdToken, accessToken, now))
                .orElseGet(() -> createNewUserInDatabase(googleIdToken, googleId, accessToken, userData, now));
    }


    private User updateGoogleDataInDatabase(User user, String googleId, String token, String accessToken, Instant now) {
        if (user.getGoogleId() == null) {
            user.setGoogleId(googleId);
        }
        user.setGoogleIdToken(token);
        user.setAccessToken(accessToken);
        user.setModifiedAt(now);
        try {
            User updatedUser = userRepository.saveAndFlush(user);
            logger.debug("GoogleLoginService - updating googleIdToken - success");
            return updatedUser;
        } catch (DataIntegrityViolationException e) {
            throw new DataInconsistencyAuthenticationException("Updating user: " + user.getEmail() + " failed. Given googleId " + user
                    .getGoogleId() + " already exists in database under different email.", e);
        }
    }


    private User createNewUserInDatabase(String googleIdToken, String googleId, String accessToken, Payload userData, Instant now) throws EmailNotVerifiedAuthenticationException {
        if (userData.getEmailVerified()) {
            UUID userId = UUID.randomUUID();
            UserPersonalData userPersonalData = new UserPersonalDataBuilder()
                    .setId(userId)
                    .setCreatedAt(now)
                    .setModifiedAt(now)
                    .setFirstName(Optional.ofNullable(userData.get("given_name"))
                                          .map(Object::toString)
                                          .orElse(null))
                    .createUserPersonalData();

            User userToSave = new UserBuilder()
                    .setId(userId)
                    .setEmail(userData.getEmail())
                    .setGoogleId(googleId)
                    .setGoogleIdToken(googleIdToken)
                    .setAccessToken(accessToken)
                    .setCreatedAt(now)
                    .setModifiedAt(now)
                    .setUserPersonalData(userPersonalData)
                    .createUser();
            User savedUser = userRepository.saveAndFlush(userToSave);
            logger.debug("GoogleLoginService - creating new User in database - success");
            return savedUser;
        } else {
            throw new EmailNotVerifiedAuthenticationException("Google registration failed. " + userData.getEmail() + " was not verified by Google.");
        }
    }


    private String createTokenIfAuthorized(UUID userId, boolean ifEverAuthorized) {
        Instant now = Instant.now();
        String token = tokenCreator.create(userId.toString(), now, ifEverAuthorized);
        logger.info("GoogleLoginService - success.");
        return token;
    }
}
