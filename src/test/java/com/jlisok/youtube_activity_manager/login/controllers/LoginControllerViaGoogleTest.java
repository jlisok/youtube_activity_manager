package com.jlisok.youtube_activity_manager.login.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.jlisok.youtube_activity_manager.login.dto.GoogleLoginRequestDto;
import com.jlisok.youtube_activity_manager.login.exceptions.DataInconsistencyAuthenticationException;
import com.jlisok.youtube_activity_manager.login.exceptions.EmailNotVerifiedAuthenticationException;
import com.jlisok.youtube_activity_manager.synchronization.services.YouTubeDataSynchronizationService;
import com.jlisok.youtube_activity_manager.testutils.*;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class LoginControllerViaGoogleTest implements TestProfile {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MockMvcBasicRequestBuilder mvcBasicRequestBuilder;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private JwtTokenVerifier jwtTokenVerifier;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private GoogleIdTokenVerifier googleIdTokenVerifier;

    @MockBean
    private YouTubeDataSynchronizationService synchronizationService;

    private final String dummyAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    private final String dummyIdToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    private final String endPointUrl = "/api/v1/login/viaGoogle";

    private String userEmail;
    private GoogleIdToken googleIdToken;
    private GoogleLoginRequestDto googleLoginRequestDto;


    @BeforeEach
    void createRandomUser() {
        userEmail = userUtils.createRandomEmail();
        googleIdToken = MockGoogleIdToken.createDummyGoogleIdToken(userEmail, true, true);
        googleLoginRequestDto = new GoogleLoginRequestDto(dummyIdToken, dummyAccessToken);
    }


    @Test
    @Transactional
    void authenticateUserWithGoogle_whenNewValidUserAndSynchronizationServiceSucceeded() throws Exception {
        //given
        when(googleIdTokenVerifier.verify(any(String.class)))
                .thenReturn(googleIdToken);

        when(synchronizationService.synchronizeAndSendToCloud(eq(dummyAccessToken), any(UUID.class)))
                .thenReturn(new CompletableFuture<>());

        //when //then
        mockMvc
                .perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, googleLoginRequestDto))
                .andExpect(status().isOk())
                .andExpect(Assertions::assertNotNull)
                .andExpect(result -> jwtTokenVerifier.assertEqualsUserIdsAndGoogleIdAndTokenNotNull(userEmail, result))
                .andExpect(result -> jwtTokenVerifier.assertEqualsTokenClaimAuthorized(true, result));


        Assertions.assertTrue(userRepository.existsByEmail(userEmail));
    }


    @Test
    @Transactional
    void authenticateUserWithGoogle_whenNewValidUserAndSynchronizationServiceFailed() throws Exception {
        //given
        when(googleIdTokenVerifier.verify(any(String.class)))
                .thenReturn(googleIdToken);

        when(synchronizationService.synchronizeAndSendToCloud(eq(dummyAccessToken), any(UUID.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException()));

        //when //then
        mockMvc
                .perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, googleLoginRequestDto))
                .andExpect(status().isOk())
                .andExpect(Assertions::assertNotNull)
                .andExpect(result -> jwtTokenVerifier.assertEqualsUserIdsAndGoogleIdAndTokenNotNull(userEmail, result))
                .andExpect(result -> jwtTokenVerifier.assertEqualsTokenClaimAuthorized(true, result));


        Assertions.assertTrue(userRepository.existsByEmail(userEmail));
    }


    @Test
    @Transactional
    void authenticateUserWithGoogle_whenNewValidUserNoFirstName() throws Exception {
        //given
        GoogleIdToken googleIdTokenNoFirstName = MockGoogleIdToken.createDummyGoogleIdToken(userEmail, true, false);
        when(googleIdTokenVerifier.verify(any(String.class)))
                .thenReturn(googleIdTokenNoFirstName);

        //when //then
        mockMvc
                .perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, googleLoginRequestDto))
                .andExpect(status().isOk())
                .andExpect(Assertions::assertNotNull)
                .andExpect(result -> jwtTokenVerifier.assertEqualsUserIdsAndGoogleIdAndTokenNotNull(userEmail, result))
                .andExpect(result -> jwtTokenVerifier.assertEqualsTokenClaimAuthorized(true, result));


        Assertions.assertTrue(userRepository.existsByEmail(userEmail));

    }


    @Test
    @Transactional
    void authenticateUserWithGoogle_whenUpdatingValidUser() throws Exception {
        //given
        userUtils.insertUserInDatabase(userEmail, userUtils.createRandomPassword());

        when(googleIdTokenVerifier.verify(any(String.class)))
                .thenReturn(googleIdToken);

        //when //then
        mockMvc
                .perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, googleLoginRequestDto))
                .andExpect(status().isOk())
                .andExpect(Assertions::assertNotNull)
                .andExpect(result -> jwtTokenVerifier.assertEqualsUserIdsAndGoogleIdAndTokenNotNull(userEmail, result))
                .andExpect(result -> jwtTokenVerifier.assertEqualsTokenClaimAuthorized(true, result));


        Assertions.assertTrue(userRepository.existsByEmail(userEmail));
    }


    @Test
    @Transactional
    void authenticateUserWithGoogle_whenUpdatingUserThatHasGoogleId() throws Exception {
        //given
        userUtils.insertUserFromTokenInDatabase(dummyIdToken, googleIdToken, dummyAccessToken);

        when(googleIdTokenVerifier.verify(any(String.class)))
                .thenReturn(googleIdToken);

        //when //then
        mockMvc
                .perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, googleLoginRequestDto))
                .andExpect(status().isOk())
                .andExpect(result -> jwtTokenVerifier.assertEqualsUserIdsAndGoogleIdAndTokenNotNull(userEmail, result))
                .andExpect(result -> jwtTokenVerifier.assertEqualsTokenClaimAuthorized(true, result));


        Assertions.assertTrue(userRepository.existsByEmail(userEmail));
    }


    @Test
    void authenticateUserWithGoogle_whenGoogleIdTokenInvalid() throws Exception {
        //given
        when(googleIdTokenVerifier.verify(any(String.class)))
                .thenReturn(null);

        //when //then
        mockMvc
                .perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, googleLoginRequestDto))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AuthenticationCredentialsNotFoundException));
    }


    @Test
    void authenticateUserWithGoogle_whenGoogleIdAlreadyExistsInDatabaseUnderDifferentEmail() throws Exception {
        //given
        userUtils.insertUserInDatabase(userEmail, userUtils.createRandomPassword());
        userUtils.insertUserInDatabaseSameGoogleIdDifferentEmail(dummyIdToken, googleIdToken, dummyAccessToken);

        when(googleIdTokenVerifier.verify(any(String.class)))
                .thenReturn(googleIdToken);

        //when //then
        mockMvc
                .perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, googleLoginRequestDto))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DataInconsistencyAuthenticationException));
    }


    @Test
    void authenticateUserWithGoogle_whenUserEmailIsUnverifiedByGoogle() throws Exception {
        //given
        GoogleIdToken googleIdTokenWithUnverifiedEmail = MockGoogleIdToken.createDummyGoogleIdToken(userEmail, false, true);
        when(googleIdTokenVerifier.verify(any(String.class)))
                .thenReturn(googleIdTokenWithUnverifiedEmail);

        //when //then
        mockMvc
                .perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, googleLoginRequestDto))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EmailNotVerifiedAuthenticationException));

        Assertions.assertFalse(userRepository.existsByEmail(userEmail));
    }


    @Test
    void authenticateUser_whenIdTokenDtoIsNull() throws Exception {
        //given //when //then
        GoogleLoginRequestDto dtoTokenNull = new GoogleLoginRequestDto(null, dummyAccessToken);

        mockMvc
                .perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, dtoTokenNull))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));

        Assertions.assertFalse(userRepository.existsByEmail(userEmail));
    }


    @Test
    void authenticateUser_whenIdTokenDtoIsBlank() throws Exception {
        //given //when //then
        GoogleLoginRequestDto dtoTokenNull = new GoogleLoginRequestDto("", dummyAccessToken);

        mockMvc
                .perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, dtoTokenNull))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));

        Assertions.assertFalse(userRepository.existsByEmail(userEmail));
    }

    @Test
    void authenticateUser_whenAccessTokenDtoIsNull() throws Exception {
        //given //when //then
        GoogleLoginRequestDto dtoTokenNull = new GoogleLoginRequestDto(dummyIdToken, null);

        mockMvc
                .perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, dtoTokenNull))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));

        Assertions.assertFalse(userRepository.existsByEmail(userEmail));
    }


    @Test
    void authenticateUser_whenAccessTokenDtoIsBlank() throws Exception {
        //given //when //then
        GoogleLoginRequestDto dtoTokenNull = new GoogleLoginRequestDto(dummyIdToken, "");

        mockMvc
                .perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, dtoTokenNull))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));

        Assertions.assertFalse(userRepository.existsByEmail(userEmail));
    }

}
