package com.jlisok.youtube_activity_manager.login.services;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.jlisok.youtube_activity_manager.login.dto.AuthenticationDto;
import com.jlisok.youtube_activity_manager.login.dto.GoogleRequestDto;
import com.jlisok.youtube_activity_manager.login.exceptions.AuthorizationException;
import com.jlisok.youtube_activity_manager.login.exceptions.DataInconsistencyAuthenticationException;
import com.jlisok.youtube_activity_manager.login.exceptions.EmailNotVerifiedAuthenticationException;
import com.jlisok.youtube_activity_manager.login.utils.GoogleTokenVerifier;
import com.jlisok.youtube_activity_manager.login.utils.JwtClaimNames;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext;
import com.jlisok.youtube_activity_manager.testutils.MockGoogleIdToken;
import com.jlisok.youtube_activity_manager.testutils.TestProfile;
import com.jlisok.youtube_activity_manager.testutils.UserTestUtils;
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
import org.springframework.dao.DataIntegrityViolationException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class GoogleAuthorizationServiceImplementationTest implements TestProfile {

    @Autowired
    private GoogleAuthorizationService googleService;

    @Autowired
    private UserTestUtils userTestUtils;

    @Autowired
    private JWTVerifier jwtVerifier;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GoogleTokenVerifier googleTokenVerifier;

    @Captor
    private ArgumentCaptor<User> captureUser;


    private static final Answer<User> interceptUser = invocation -> (User) invocation.getArguments()[0];
    private final String dbGoogleIdToken = "OldDummyIdTokenOldDummyIdToken";
    private final GoogleRequestDto dto = new GoogleRequestDto("DummyIdTokenDummyIdToken", "DummyAccessTokenDummyAccessToken");

    private User user;
    private GoogleIdToken googleIdToken;


    @BeforeEach
    void createInitialConditions() throws RegistrationException {
        user = userTestUtils.createUser(userTestUtils.createRandomEmail(), userTestUtils.createRandomPassword());
        googleIdToken = MockGoogleIdToken.createDummyGoogleIdToken(userTestUtils.createRandomEmail(), true, true);
        JwtAuthenticationContext.setAuthenticationInContext("dummyJwToken", user.getId());
    }


    @Test
    void authorizeUser_whenUserNeverAuthorized() throws GeneralSecurityException, IOException {
        //given
        when(googleTokenVerifier.verifyGoogleIdToken(dto.getGoogleIdToken()))
                .thenReturn(googleIdToken);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(interceptUser);

        //when
        AuthenticationDto authenticationDto = googleService.authorizeUser(dto);
        DecodedJWT decodedJWT = jwtVerifier.verify(authenticationDto.getJwtToken());

        //then
        verify(userRepository).saveAndFlush(captureUser.capture());
        var dbUser = captureUser.getValue();

        Assertions.assertEquals(dto.getGoogleIdToken(), dbUser.getGoogleIdToken());
        Assertions.assertEquals(googleIdToken.getPayload().getSubject(), dbUser.getGoogleId());
        Assertions.assertTrue(decodedJWT.getClaim(JwtClaimNames.AUTHORIZED).asBoolean());
        Assertions.assertEquals(user.getId().toString(), decodedJWT.getSubject());
    }


    @Test
    void authorizeUser_whenUserWasPreviouslyAuthorized() throws GeneralSecurityException, IOException {
        //given
        user.setGoogleIdToken(dbGoogleIdToken);
        user.setGoogleId(googleIdToken.getPayload().getSubject());

        when(googleTokenVerifier.verifyGoogleIdToken(dto.getGoogleIdToken()))
                .thenReturn(googleIdToken);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(interceptUser);

        //when
        AuthenticationDto authenticationDto = googleService.authorizeUser(dto);
        DecodedJWT decodedJWT = jwtVerifier.verify(authenticationDto.getJwtToken());

        //then
        verify(userRepository).saveAndFlush(captureUser.capture());
        var dbUser = captureUser.getValue();

        Assertions.assertEquals(dto.getGoogleIdToken(), dbUser.getGoogleIdToken());
        Assertions.assertEquals(user.getGoogleId(), dbUser.getGoogleId());
        Assertions.assertTrue(decodedJWT.getClaim(JwtClaimNames.AUTHORIZED).asBoolean());
        Assertions.assertEquals(user.getId().toString(), decodedJWT.getSubject());
    }


    @Test
    void authorizeUser_whenGoogleAccountNotVerified() throws GeneralSecurityException, IOException {
        //given
        var googleIdTokenEmailNotVerified = MockGoogleIdToken.createDummyGoogleIdToken(userTestUtils.createRandomEmail(), false, true);

        when(googleTokenVerifier.verifyGoogleIdToken(dto.getGoogleIdToken()))
                .thenReturn(googleIdTokenEmailNotVerified);

        //when //then
        Assertions.assertThrows(EmailNotVerifiedAuthenticationException.class, () -> googleService.authorizeUser(dto));
    }


    @Test
    void authorizeUser_whenUserDoesNotExistInDatabase() throws GeneralSecurityException, IOException {
        //given
        when(googleTokenVerifier.verifyGoogleIdToken(dto.getGoogleIdToken()))
                .thenReturn(googleIdToken);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        //when //then
        Assertions.assertThrows(AuthorizationException.class, () -> googleService.authorizeUser(dto));
    }


    @Test
    void authorizeUser_whenUserPreviouslyAuthorizedWithDifferentGoogleAccount() throws GeneralSecurityException, IOException {
        //given
        user.setGoogleIdToken(dbGoogleIdToken);
        user.setGoogleId(userTestUtils.createRandomGoogleId());

        when(googleTokenVerifier.verifyGoogleIdToken(dto.getGoogleIdToken()))
                .thenReturn(googleIdToken);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        //when //then
        Assertions.assertThrows(DataInconsistencyAuthenticationException.class, () -> googleService.authorizeUser(dto));
    }


    @Test
    void authorizeUser_whenOtherUserAuthorizedWithGoogleAccount() throws GeneralSecurityException, IOException {
        //given
        when(googleTokenVerifier.verifyGoogleIdToken(dto.getGoogleIdToken()))
                .thenReturn(googleIdToken);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("User already exists in database"));

        //when //then
        Assertions.assertThrows(DataInconsistencyAuthenticationException.class, () -> googleService.authorizeUser(dto));
    }
}