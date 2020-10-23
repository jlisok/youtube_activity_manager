package com.jlisok.youtube_activity_manager.login.services;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.jlisok.youtube_activity_manager.login.dto.AuthenticationDto;
import com.jlisok.youtube_activity_manager.login.dto.GoogleLoginRequestDto;
import com.jlisok.youtube_activity_manager.login.exceptions.EmailNotVerifiedAuthenticationException;
import com.jlisok.youtube_activity_manager.testutils.MockGoogleIdToken;
import com.jlisok.youtube_activity_manager.testutils.TestProfile;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class GoogleLoginServiceTest implements TestProfile {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GoogleIdTokenVerifier verifier;

    @Autowired
    private JWTVerifier jwtVerifier;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private GoogleLoginService service;

    @Captor
    ArgumentCaptor<User> userCapture;

    private final String dummyToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    private final String dummyAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOi";
    private GoogleLoginRequestDto dto;
    private GoogleIdToken googleIdToken;
    private String email;


    @BeforeEach
    void createInitialVariablesForTestsAndMocks() {
        email = userUtils.createRandomEmail();
        dto = new GoogleLoginRequestDto(dummyToken, dummyAccessToken);
        googleIdToken = MockGoogleIdToken.createDummyGoogleIdToken(email, true, true);
    }


    private static final Answer<User> interceptUser = invocation -> (User) invocation.getArguments()[0];

    @Test
    void authenticateUser_whenNewValidUser() throws Exception {
        //given
        when(verifier.verify(dto.getGoogleIdToken()))
                .thenReturn(googleIdToken);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(interceptUser);

        //when
        AuthenticationDto authenticationDto = service.authenticateUser(dto);

        //then
        assertNotNull(authenticationDto);
        assertNotNull(authenticationDto.getJwtToken());
        DecodedJWT decodedJWT = jwtVerifier.verify(authenticationDto.getJwtToken());

        verify(userRepository).saveAndFlush(userCapture.capture());

        assertEquals(userCapture.getValue().getId().toString(), decodedJWT.getSubject());
    }


    @Test
    void authenticateUser_whenUpdatingValidUser() throws Exception {
        //given
        User user = userUtils.createUserWithDataFromToken(email, "dummyPassword");

        when(verifier.verify(dto.getGoogleIdToken()))
                .thenReturn(googleIdToken);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(interceptUser);


        //when
        AuthenticationDto authenticationDto = service.authenticateUser(dto);

        //then
        assertNotNull(authenticationDto);
        assertNotNull(authenticationDto.getJwtToken());
        verify(userRepository).saveAndFlush(userCapture.capture());
        DecodedJWT decodedJWT = jwtVerifier.verify(authenticationDto.getJwtToken());
        assertEquals(userCapture.getValue().getId().toString(), decodedJWT.getSubject());
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
    void authenticateUser_whenUserWithGoogleIdPresent() throws Exception {
        //given
        User user = userUtils.createUserWithDataFromToken(dummyToken, googleIdToken, dummyAccessToken);

        Assertions.assertNotNull(user.getGoogleId());

        when(verifier.verify(dto.getGoogleIdToken()))
                .thenReturn(googleIdToken);

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(interceptUser);

        //when
        AuthenticationDto authenticationDto = service.authenticateUser(dto);

        //then
        assertNotNull(authenticationDto);
        assertNotNull(authenticationDto.getJwtToken());
        verify(userRepository).saveAndFlush(userCapture.capture());
        DecodedJWT decodedJWT = jwtVerifier.verify(authenticationDto.getJwtToken());
        assertEquals(userCapture.getValue().getId().toString(), decodedJWT.getSubject());
    }


    @Test
    void authenticateUser_whenEmailNotVerifiedByGoogle() throws Exception {
        //given
        GoogleIdToken tokenEmailNotVerified = MockGoogleIdToken.createDummyGoogleIdToken(email, false, true);

        when(verifier.verify(dto.getGoogleIdToken()))
                .thenReturn(tokenEmailNotVerified);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        //when //then
        assertThrows(EmailNotVerifiedAuthenticationException.class, () -> service.authenticateUser(dto));
    }
}