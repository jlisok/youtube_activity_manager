package com.jlisok.youtube_activity_manager.login.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.jlisok.youtube_activity_manager.login.dto.GoogleRequestDto;
import com.jlisok.youtube_activity_manager.login.exceptions.DataInconsistencyAuthenticationException;
import com.jlisok.youtube_activity_manager.login.exceptions.EmailNotVerifiedAuthenticationException;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.testutils.*;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerAuthorizationTest implements TestProfile {

    @Autowired
    private AuthenticationUtils authenticationUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTestUtils userTestUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MockMvcBasicRequestBuilder mvcBasicRequestBuilder;

    @Autowired
    private JwtTokenVerifier jwtTokenVerifier;

    @MockBean
    private GoogleIdTokenVerifier googleIdTokenVerifier;

    private final String endPoint = "/api/v1/login/authorize";
    private final GoogleRequestDto dto = new GoogleRequestDto("DummyIdTokenDummyIdToken", "DummyAccessTokenDummyAccessToken");
    private final String dbGoogleIdToken = "OldDummyIdTokenOldDummyIdToken";

    private String jsonHeader;
    private User user;
    private GoogleIdToken googleIdToken;

    @BeforeEach
    @Transactional
    void createInitialConditions() throws RegistrationException {
        user = userTestUtils.insertUserInDatabase(userTestUtils.createRandomEmail(), userTestUtils.createRandomPassword());
        googleIdToken = MockGoogleIdToken.createDummyGoogleIdToken(userTestUtils.createRandomEmail(), true, true);
        jsonHeader = authenticationUtils.createRequestAuthenticationHeader(user.getId().toString(), true);
    }

    @Test
    @Transactional
    void authorizeUser_whenUserPreviouslyNotAuthorized() throws Exception {
        //given

        when(googleIdTokenVerifier.verify(any(String.class)))
                .thenReturn(googleIdToken);

        //when //then
        mockMvc
                .perform(mvcBasicRequestBuilder.setPostRequestWithAuthorizationHeader(endPoint, jsonHeader, dto))
                .andExpect(status().isOk())
                .andExpect(Assertions::assertNotNull)
                .andExpect(result -> jwtTokenVerifier.assertUserIdsAndGoogleIdEqual(user.getEmail(), result, googleIdToken))
                .andExpect(result -> jwtTokenVerifier.assertEqualsTokenClaimAuthorized(true, result));
    }


    @Test
    @Transactional
    void authorizeUser_whenUserPreviouslyAuthorized() throws Exception {
        //given
        user.setGoogleIdToken(dbGoogleIdToken);
        user.setGoogleId(googleIdToken.getPayload().getSubject());

        when(googleIdTokenVerifier.verify(any(String.class)))
                .thenReturn(googleIdToken);

        //when //then
        mockMvc
                .perform(mvcBasicRequestBuilder.setPostRequestWithAuthorizationHeader(endPoint, jsonHeader, dto))
                .andExpect(status().isOk())
                .andExpect(Assertions::assertNotNull)
                .andExpect(result -> jwtTokenVerifier.assertUserIdsAndGoogleIdEqual(user.getEmail(), result, googleIdToken))
                .andExpect(result -> jwtTokenVerifier.assertEqualsTokenClaimAuthorized(true, result));
    }

    @Test
    @Transactional
    void authorizeUser_whenGoogleEmailNotVerified() throws Exception {
        //given
        var googleIdTokenEmailNotVerified = MockGoogleIdToken.createDummyGoogleIdToken(userTestUtils.createRandomEmail(), false, true);

        when(googleIdTokenVerifier.verify(any(String.class)))
                .thenReturn(googleIdTokenEmailNotVerified);

        //when //then
        mockMvc
                .perform(mvcBasicRequestBuilder.setPostRequestWithAuthorizationHeader(endPoint, jsonHeader, dto))
                .andExpect(status().isForbidden())
                .andExpect(Assertions::assertNotNull)
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EmailNotVerifiedAuthenticationException));
    }


    @Test
    @Transactional
    void authorizeUser_whenUserPreviouslyAuthorizedWithDifferentGoogleAccount() throws Exception {
        //given
        user.setGoogleId(userTestUtils.createRandomGoogleId());
        user.setGoogleIdToken(dbGoogleIdToken);
        user.setAccessToken(dbGoogleIdToken);
        userRepository.saveAndFlush(user);

        when(googleIdTokenVerifier.verify(any(String.class)))
                .thenReturn(googleIdToken);

        //when //then
        mockMvc
                .perform(mvcBasicRequestBuilder.setPostRequestWithAuthorizationHeader(endPoint, jsonHeader, dto))
                .andExpect(status().isForbidden())
                .andExpect(Assertions::assertNotNull)
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DataInconsistencyAuthenticationException));
    }


    @Test
    @Transactional
    void authorizeUser_whenOtherUserAuthorizedWithGoogleAccount() throws Exception {
        //given
        var otherUser = userTestUtils.createUser(userTestUtils.createRandomEmail(), userTestUtils.createRandomPassword());
        otherUser.setGoogleId(googleIdToken.getPayload().getSubject());
        otherUser.setGoogleIdToken(dbGoogleIdToken);
        otherUser.setAccessToken(dbGoogleIdToken);
        userRepository.saveAndFlush(otherUser);

        when(googleIdTokenVerifier.verify(any(String.class)))
                .thenReturn(googleIdToken);

        //when //then
        mockMvc
                .perform(mvcBasicRequestBuilder.setPostRequestWithAuthorizationHeader(endPoint, jsonHeader, dto))
                .andExpect(status().isForbidden())
                .andExpect(Assertions::assertNotNull)
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DataInconsistencyAuthenticationException));
    }

}