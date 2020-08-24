package com.jlisok.youtube_activity_manager.login.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlisok.youtube_activity_manager.login.dto.LoginRequestDto;
import com.jlisok.youtube_activity_manager.registration.dto.RegistrationRequestDto;
import com.jlisok.youtube_activity_manager.testutils.RandomRegistrationDto;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;


    private String userEmail;
    private String userPassword;
    private RegistrationRequestDto validRegistrationRequest;

    @BeforeEach
    void createRandomUser() {
        userEmail = UserUtils.createRandomEmail();
        userPassword = UserUtils.createRandomPassword();
        validRegistrationRequest = RandomRegistrationDto.createValidRegistrationDto(userEmail, userPassword);
    }


    @Test
    @Transactional
    void authenticateUser_whenUserExistsAndRequestIsValid() throws Exception {
        //given
        User user = UserUtils.createUserInDatabase(validRegistrationRequest);
        LoginRequestDto validLoginRequestDto = new LoginRequestDto(userPassword, userEmail);

        assertTrue(userRepository
                .findByEmail(user.getEmail())
                .isPresent());


        //when //then
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/login")
                        .content(objectMapper.writeValueAsString(validLoginRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(Assertions::assertNotNull);
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
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof FailedLoginException));
    }


    @Test
    @Transactional
    void authenticateUser_whenUserSendsBadPassword() throws Exception {
        //given
        User user = UserUtils.createUserInDatabase(validRegistrationRequest);
        LoginRequestDto loginRequestDtoBadPassword = new LoginRequestDto("some_other_password", userEmail);

        assertTrue(userRepository
                .findByEmail(user.getEmail())
                .isPresent());

        //when //then
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/login")
                        .content(objectMapper.writeValueAsString(loginRequestDtoBadPassword))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof FailedLoginException));
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