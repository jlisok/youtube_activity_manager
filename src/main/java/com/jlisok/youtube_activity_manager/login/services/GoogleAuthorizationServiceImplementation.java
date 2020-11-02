package com.jlisok.youtube_activity_manager.login.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.jlisok.youtube_activity_manager.login.dto.AuthenticationDto;
import com.jlisok.youtube_activity_manager.login.dto.GoogleRequestDto;
import com.jlisok.youtube_activity_manager.login.exceptions.AuthorizationException;
import com.jlisok.youtube_activity_manager.login.exceptions.DataInconsistencyAuthenticationException;
import com.jlisok.youtube_activity_manager.login.exceptions.EmailNotVerifiedAuthenticationException;
import com.jlisok.youtube_activity_manager.login.utils.GoogleTokenVerifier;
import com.jlisok.youtube_activity_manager.login.utils.TokenCreator;
import com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import com.jlisok.youtube_activity_manager.users.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Optional;

@Service
public class GoogleAuthorizationServiceImplementation implements GoogleAuthorizationService {


    private final UserRepository userRepository;
    private final UserUtils userUtils;
    private final GoogleTokenVerifier googleIdTokenVerifier;

    private final TokenCreator tokenCreator;

    @Autowired
    public GoogleAuthorizationServiceImplementation(UserRepository userRepository, UserUtils userUtils, GoogleTokenVerifier googleIdTokenVerifier, TokenCreator tokenCreator) {
        this.userRepository = userRepository;
        this.userUtils = userUtils;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
        this.tokenCreator = tokenCreator;
    }


    @Override
    public AuthenticationDto authorizeUser(GoogleRequestDto dto) throws GeneralSecurityException, IOException {
        GoogleIdToken googleIdToken = googleIdTokenVerifier.verifyGoogleIdToken(dto.getGoogleIdToken());
        if (googleIdToken.getPayload().getEmailVerified()) {
            User user = updateUser(googleIdToken, dto);
            var jwToken = tokenCreator.create(user.getId().toString(), Instant.now(), user.checkIfEverAuthorized());
            return new AuthenticationDto(user.getId(), jwToken);
        } else {
            var userId = JwtAuthenticationContext.getAuthenticationInContext().getAuthenticatedUserId();
            throw new EmailNotVerifiedAuthenticationException("UserId " + userId + " authorization failed. " + googleIdToken
                    .getPayload()
                    .getEmail() + " was not verified by Google.");
        }
    }


    private User updateUser(GoogleIdToken googleIdToken, GoogleRequestDto dto) {
        var userId = JwtAuthenticationContext.getAuthenticationInContext().getAuthenticatedUserId();
        var googleId = googleIdToken.getPayload().getSubject();
        return userRepository
                .findById(userId)
                .map(user -> checkIfDbPayloadSubjectEquals(user, googleIdToken.getPayload()))
                .map(user -> userUtils.updateGoogleDataInDatabase(user, googleId, dto.getGoogleIdToken(), dto
                        .getAccessToken(), Instant.now()))
                .orElseThrow(() -> new AuthorizationException("Authorizing userId " + userId + " failed. Could not find user in database."));
    }


    private User checkIfDbPayloadSubjectEquals(User user, GoogleIdToken.Payload payload) {
        return Optional
                .ofNullable(user.getGoogleId())
                .map(googleId -> {
                    if (googleId.equals(payload.getSubject())) {
                        return user;
                    } else {
                        throw new DataInconsistencyAuthenticationException("Authorizing userId " + user.getId() + " failed. Google account inconsistent with database.");
                    }
                })
                .orElse(user);
    }
}
