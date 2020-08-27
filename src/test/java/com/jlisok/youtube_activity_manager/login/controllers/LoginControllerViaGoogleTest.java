package com.jlisok.youtube_activity_manager.login.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.jlisok.youtube_activity_manager.login.dto.GoogleLoginRequestDto;
import com.jlisok.youtube_activity_manager.login.exceptions.DataInconsistencyAuthenticationException;
import com.jlisok.youtube_activity_manager.login.exceptions.EmailNotVerifiedAuthenticationException;
import com.jlisok.youtube_activity_manager.testutils.JwtTokenVerifier;
import com.jlisok.youtube_activity_manager.testutils.MockGoogleIdToken;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoginControllerViaGoogleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private JwtTokenVerifier mvcToken;

    @MockBean
    private GoogleIdTokenVerifier verifier;

    private String userEmail;
    private final String dummyIdToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    private final String dummyAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
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
    void authenticateUserWithGoogle_whenNewValidUser() throws Exception {
        //given
        when(verifier.verify(any(String.class)))
                .thenReturn(googleIdToken);

        //when //then
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/login/viaGoogle")
                                .content(objectMapper.writeValueAsString(googleLoginRequestDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(Assertions::assertNotNull)
                .andExpect(result ->
                                   mvcToken.assertEqualsTokenSubjectAndGoogleIdNotNull(userEmail, result));
    }


    @Test
    @Transactional
    void authenticateUserWithGoogle_whenNewValidUserNoFirstName() throws Exception {
        //given
        GoogleIdToken googleIdTokenNoFirstName = MockGoogleIdToken.createDummyGoogleIdToken(userEmail, true, false);
        when(verifier.verify(any(String.class)))
                .thenReturn(googleIdTokenNoFirstName);

        //when //then
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/login/viaGoogle")
                                .content(objectMapper.writeValueAsString(googleLoginRequestDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(Assertions::assertNotNull)
                .andExpect(result ->
                                   mvcToken.assertEqualsTokenSubjectAndGoogleIdNotNull(userEmail, result));
    }


    @Test
    @Transactional
    void authenticateUserWithGoogle_whenUpdatingValidUser() throws Exception {
        //given
        userUtils.insertUserInDatabase(userEmail, userUtils.createRandomPassword());

        when(verifier.verify(any(String.class)))
                .thenReturn(googleIdToken);

        //when //then
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/login/viaGoogle")
                                .content(objectMapper.writeValueAsString(googleLoginRequestDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(Assertions::assertNotNull)
                .andExpect(result -> mvcToken.assertEqualsTokenSubjectAndGoogleIdNotNull(userEmail, result));
    }


    @Test
    void authenticateUserWithGoogle_whenGoogleIdTokenInvalid() throws Exception {
        //given
        when(verifier.verify(any(String.class)))
                .thenReturn(null);

        //when //then
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/login/viaGoogle")
                                .content(objectMapper.writeValueAsString(googleLoginRequestDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AuthenticationCredentialsNotFoundException));
    }


    @Test
    @Transactional
    void authenticateUserWithGoogle_whenUserExistsButGoogleIdAlreadyExistsInDatabaseUnderDifferentEmail() throws Exception {
        //given
        userUtils.insertUserInDatabase(userEmail, userUtils.createRandomPassword());
        userUtils.insertUserInDatabaseSameGoogleIdDifferentEmail(dummyIdToken, googleIdToken);

        when(verifier.verify(any(String.class)))
                .thenReturn(googleIdToken);

        //when //then
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/login/viaGoogle")
                                .content(objectMapper.writeValueAsString(googleLoginRequestDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DataInconsistencyAuthenticationException));
    }


    @Test
    @Transactional
    void authenticateUserWithGoogle_whenUserEmailIsUnverifiedByGoogle() throws Exception {
        //given
        GoogleIdToken googleIdTokenWithUnverifiedEmail = MockGoogleIdToken.createDummyGoogleIdToken(userEmail, false, true);
        when(verifier.verify(any(String.class)))
                .thenReturn(googleIdTokenWithUnverifiedEmail);

        //when //then
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/login/viaGoogle")
                                .content(objectMapper.writeValueAsString(googleLoginRequestDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EmailNotVerifiedAuthenticationException));
    }


    @Test
    void authenticateUser_whenIdTokenDtoIsNull() throws Exception {
        //given //when //then
        GoogleLoginRequestDto dtoTokenNull = new GoogleLoginRequestDto(null, dummyAccessToken);

        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/login/viaGoogle")
                                .content(objectMapper.writeValueAsString(dtoTokenNull))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }


    @Test
    void authenticateUser_whenIdTokenDtoIsBlank() throws Exception {
        //given //when //then
        GoogleLoginRequestDto dtoTokenNull = new GoogleLoginRequestDto("", dummyAccessToken);

        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/login/viaGoogle")
                                .content(objectMapper.writeValueAsString(dtoTokenNull))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void authenticateUser_whenAccessTokenDtoIsNull() throws Exception {
        //given //when //then
        GoogleLoginRequestDto dtoTokenNull = new GoogleLoginRequestDto(dummyIdToken, null);

        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/login/viaGoogle")
                                .content(objectMapper.writeValueAsString(dtoTokenNull))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }


    @Test
    void authenticateUser_whenAccessTokenDtoIsBlank() throws Exception {
        //given //when //then
        GoogleLoginRequestDto dtoTokenNull = new GoogleLoginRequestDto(dummyIdToken, "");

        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/login/viaGoogle")
                                .content(objectMapper.writeValueAsString(dtoTokenNull))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

}
