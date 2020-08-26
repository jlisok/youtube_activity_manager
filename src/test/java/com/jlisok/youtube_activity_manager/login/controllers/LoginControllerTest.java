package com.jlisok.youtube_activity_manager.login.controllers;

import com.auth0.jwt.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlisok.youtube_activity_manager.domain.exceptions.ResponseCode;
import com.jlisok.youtube_activity_manager.login.dto.LoginRequestDto;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.security.auth.login.FailedLoginException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JWTVerifier jwtVerifier;

    @Autowired
    private UserUtils userUtils;


    private String userEmail;
    private String userPassword;

    @BeforeEach
    void createRandomUser() {
        userEmail = userUtils.createRandomEmail();
        userPassword = userUtils.createRandomPassword();
    }


    @Test
    @Transactional
    void authenticateUser_whenUserExistsAndRequestIsValid() throws Exception {
        //given
        User user = userUtils.createUserInDatabase(userEmail, userPassword);
        LoginRequestDto validLoginRequestDto = new LoginRequestDto(userPassword, userEmail);


        //when //then
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/login")
                        .content(objectMapper.writeValueAsString(validLoginRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(Assertions::assertNotNull)
                .andExpect(result ->
                        assertEquals(user.getId().toString(), jwtVerifier
                                .verify(result
                                        .getResponse()
                                        .getContentAsString()
                                )
                                .getSubject()
                        )
                );
    }


    @Test
    @Transactional
    void authenticateUser_whenUserNotPresentInDatabase() throws Exception {
        //given
        LoginRequestDto loginRequestDtoNotExistingUser = new LoginRequestDto(userPassword, userEmail);

        //when //then
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/login")
                        .content(objectMapper.writeValueAsString(loginRequestDtoNotExistingUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof FailedLoginException))
                .andExpect(result ->
                        assertTrue(result
                                .getResponse()
                                .getContentAsString()
                                .contains(
                                        ResponseCode
                                                .LOGIN_FAILED_PARAMETERS_DO_NOT_MATCH_DATABASE
                                                .toString()
                                )
                        )
                );
    }


    @Test
    @Transactional
    void authenticateUser_whenUserSendsBadPassword() throws Exception {
        //given
        userUtils.createUserInDatabase(userEmail, userPassword);
        LoginRequestDto loginRequestDtoBadPassword = new LoginRequestDto("some_other_password", userEmail);


        //when //then
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/login")
                        .content(objectMapper.writeValueAsString(loginRequestDtoBadPassword))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof FailedLoginException))
                .andExpect(result ->
                        assertTrue(result
                                .getResponse()
                                .getContentAsString()
                                .contains(
                                        ResponseCode
                                                .LOGIN_FAILED_PARAMETERS_DO_NOT_MATCH_DATABASE
                                                .toString()
                                )
                        )
                );
    }


    @Test
    @Transactional
    void addUser_whenUserRequestIsNull() throws Exception {
        //given //when //then
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/login")
                        .content(objectMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException));
    }


    @ParameterizedTest
    @MethodSource("authenticateUser_whenUserFailsValidationTestData")
    @Transactional
    void addUser_whenUserFailsValidation(LoginRequestDto loginRequestDto) throws Exception {
        //given //when //then
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/login")
                        .content(objectMapper.writeValueAsString(loginRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }


    Stream<Arguments> authenticateUser_whenUserFailsValidationTestData() {

        return Stream.of(
                //1
                Arguments.arguments(new LoginRequestDto(null, userEmail)),
                Arguments.arguments(new LoginRequestDto("", userEmail)),
                Arguments.arguments(new LoginRequestDto(userPassword, null)),
                Arguments.arguments(new LoginRequestDto(userPassword, "")),
                Arguments.arguments(new LoginRequestDto(userPassword, "something_without_at"))
        );
    }
}