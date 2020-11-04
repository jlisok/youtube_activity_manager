package com.jlisok.youtube_activity_manager.login.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.jlisok.youtube_activity_manager.login.dto.AuthenticationDto;
import com.jlisok.youtube_activity_manager.login.dto.GoogleRequestDto;
import com.jlisok.youtube_activity_manager.login.exceptions.EmailNotVerifiedAuthenticationException;
import com.jlisok.youtube_activity_manager.login.utils.GoogleTokenVerifier;
import com.jlisok.youtube_activity_manager.login.utils.TokenCreator;
import com.jlisok.youtube_activity_manager.userPersonalData.models.UserPersonalData;
import com.jlisok.youtube_activity_manager.userPersonalData.models.UserPersonalDataBuilder;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.models.UserBuilder;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import com.jlisok.youtube_activity_manager.users.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final UserUtils userUtils;
    private final TokenCreator tokenCreator;
    private final GoogleTokenVerifier googleIdTokenVerifier;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public GoogleLoginServiceImplementation(UserRepository userRepository, UserUtils userUtils, TokenCreator tokenCreator, GoogleTokenVerifier googleIdTokenVerifier) {
        this.userRepository = userRepository;
        this.userUtils = userUtils;
        this.tokenCreator = tokenCreator;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
    }


    @Override
    @Transactional
    public AuthenticationDto authenticateUser(GoogleRequestDto dto) throws GeneralSecurityException, IOException, AuthenticationException {
        String stringGoogleIdToken = dto.getGoogleIdToken();
        GoogleIdToken googleIdToken = googleIdTokenVerifier.verifyGoogleIdToken(stringGoogleIdToken);
        logger.debug("GoogleLoginService - googleIdToken verified.");
        User user = verifyIfUserInDatabaseOrCreateNewUser(stringGoogleIdToken, dto.getAccessToken(), googleIdToken.getPayload());
        var jwtToken = tokenCreator.create(user.getId().toString(), Instant.now(), user.checkIfEverAuthorized());
        logger.info("GoogleLoginService - success.");
        return new AuthenticationDto(user.getId(), jwtToken);
    }


    private User verifyIfUserInDatabaseOrCreateNewUser(String googleIdToken, String accessToken, Payload userData) {
        Instant now = Instant.now();
        String googleId = userData.getSubject();
        return userRepository
                .findByEmail(userData.getEmail())
                .map(user -> userUtils.updateGoogleDataInDatabase(user, googleId, googleIdToken, accessToken, now))
                .orElseGet(() -> createNewUserInDatabase(googleIdToken, googleId, accessToken, userData, now));
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
}
