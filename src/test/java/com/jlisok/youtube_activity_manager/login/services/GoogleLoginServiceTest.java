package com.jlisok.youtube_activity_manager.login.services;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.jlisok.youtube_activity_manager.login.dto.GoogleLoginRequestDto;
import com.jlisok.youtube_activity_manager.login.exceptions.DataInconsistencyAuthenticationException;
import com.jlisok.youtube_activity_manager.login.exceptions.EmailNotVerifiedAuthenticationException;
import com.jlisok.youtube_activity_manager.testutils.MockGoogleIdTokenVerifier;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class GoogleLoginServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GoogleIdTokenVerifier verifier;

    @Autowired
    private JWTVerifier jwtVerifier;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private GoogleLoginServiceImplementation service;

    private final String dummyToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    private GoogleLoginRequestDto dto;
    private GoogleIdToken token;
    private String email;


    @BeforeEach
    void createInitialVariablesForTestsAndMocks() {
        email = userUtils.createRandomEmail();
        dto = new GoogleLoginRequestDto(dummyToken);
        token = MockGoogleIdTokenVerifier.createDummyGoogleIdToken(email, true);
    }


    @Test
    void authenticateUser_whenNewValidUser() throws Exception {
        //given
        User user = userUtils.createUser(dummyToken, token);

        when(verifier.verify(any(String.class)))
                .thenReturn(token);

        when(userRepository.findByEmail(any(String.class)))
                .thenReturn(Optional.empty());

        when(userRepository.saveAndFlush(any(User.class)))
                .thenReturn(user);

        //when
        String token = service.authenticateUser(dto);
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        //then
        assertNotNull(token);
        assertEquals(user.getId().toString(), decodedJWT.getSubject());
    }


    @Test
    void authenticateUser_whenUpdatingValidUser() throws Exception {
        //given
        User user = userUtils.createUser(email, "dummyPassword");
        User expectedUser = userUtils.createUser(dummyToken, token);

        when(verifier.verify(any(String.class)))
                .thenReturn(token);

        when(userRepository.findByEmail(any(String.class)))
                .thenReturn(Optional.of(user));

        when(userRepository.saveAndFlush(any(User.class)))
                .thenReturn(expectedUser);

        //when
        String token = service.authenticateUser(dto);
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        //then
        assertNotNull(token);
        assertEquals(expectedUser.getId().toString(), decodedJWT.getSubject());
    }


    @Test
    void authenticateUser_whenGoogleTokenIsNull() throws Exception {
        //given
        when(verifier.verify(any(String.class)))
                .thenReturn(null);

        //when //then
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> service.authenticateUser(dto));
    }


    @Test
    void authenticateUser_whenGoogleIdAlreadyPresentUnderDifferentEmail() throws Exception {
        //given
        User user = userUtils.createUser(dummyToken, token);
        User userWithSameGoogleIdButDifferentEmail = userUtils.createUserWithSameGoogleIdButDifferentEmailAndId(user);

        when(verifier.verify(any(String.class)))
                .thenReturn(token);

        when(userRepository.findByEmail(any(String.class)))
                .thenReturn(Optional.of(user));

        when(userRepository.findByGoogleId(any(UUID.class)))
                .thenReturn(Optional.of(userWithSameGoogleIdButDifferentEmail));

        //when //then
        assertThrows(DataInconsistencyAuthenticationException.class, () -> service.authenticateUser(dto));
    }


    @Test
    void authenticateUser_whenEmailNotVerifiedByGoogle() throws Exception {
        //given
        GoogleIdToken tokenEmailNotVerified = MockGoogleIdTokenVerifier.createDummyGoogleIdToken(email, false);

        when(verifier.verify(any(String.class)))
                .thenReturn(tokenEmailNotVerified);

        when(userRepository.findByEmail(any(String.class)))
                .thenReturn(Optional.empty());

        //when //then
        assertThrows(EmailNotVerifiedAuthenticationException.class, () -> service.authenticateUser(dto));
    }
}