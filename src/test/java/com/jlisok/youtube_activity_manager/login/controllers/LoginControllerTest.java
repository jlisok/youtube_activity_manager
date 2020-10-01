package com.jlisok.youtube_activity_manager.login.controllers;

import com.jlisok.youtube_activity_manager.domain.exceptions.ResponseCode;
import com.jlisok.youtube_activity_manager.login.dto.LoginRequestDto;
import com.jlisok.youtube_activity_manager.testutils.JwtTokenVerifier;
import com.jlisok.youtube_activity_manager.testutils.MockMvcBasicRequestBuilder;
import com.jlisok.youtube_activity_manager.testutils.MvcResponseVerifier;
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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
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
    private MockMvcBasicRequestBuilder mvcBasicRequestBuilder;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private MvcResponseVerifier responseVerifier;

    @Autowired
    private JwtTokenVerifier jwtTokenVerifier;

    private final String endPointUrl = "/api/v1/login";

    private String userEmail;
    private String userPassword;

    @BeforeEach
    void createRandomUser() {
        userEmail = userUtils.createRandomEmail();
        userPassword = userUtils.createRandomPassword();
    }


    @Test
    @Transactional
    void authenticateUserTraditionally_whenUserExistsAndRequestIsValid() throws Exception {
        //given
        User user = userUtils.insertUserInDatabase(userEmail, userPassword);
        LoginRequestDto validLoginRequestDto = new LoginRequestDto(userPassword, userEmail);
        String expectedTokenSubject = user
                .getId()
                .toString();

        //when //then
        mockMvc
                .perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, validLoginRequestDto))
                .andExpect(status().isOk())
                .andExpect(Assertions::assertNotNull)
                .andExpect(result -> jwtTokenVerifier.assertEqualsTokenSubject(expectedTokenSubject, result));
    }


    @Test
    @Transactional
    void authenticateUserTraditionally_whenUserNotPresentInDatabase() throws Exception {
        //given
        LoginRequestDto loginRequestDtoNotExistingUser = new LoginRequestDto(userPassword, userEmail);
        ResponseCode expected = ResponseCode.LOGIN_FAILED_PARAMETERS_DO_NOT_MATCH_DATABASE;

        //when //then
        mockMvc
                .perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, loginRequestDtoNotExistingUser))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof FailedLoginException))
                .andExpect(result -> responseVerifier.assertEqualsResponseCode(expected, result));
    }


    @Test
    @Transactional
    void authenticateUserTraditionally_whenUserSendsBadPassword() throws Exception {
        //given
        userUtils.insertUserInDatabase(userEmail, userPassword);
        LoginRequestDto loginRequestDtoBadPassword = new LoginRequestDto("some_other_password", userEmail);
        ResponseCode expected = ResponseCode.LOGIN_FAILED_PARAMETERS_DO_NOT_MATCH_DATABASE;


        //when //then
        mockMvc
                .perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, loginRequestDtoBadPassword))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof FailedLoginException))
                .andExpect(result -> responseVerifier.assertEqualsResponseCode(expected, result));
    }


    @Test
    @Transactional
    void authenticateUserTraditionally_whenUserRequestIsNull() throws Exception {
        //given //when //then
        mockMvc
                .perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, null))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException));
    }


    @ParameterizedTest
    @MethodSource("authenticateUser_whenUserFailsValidationTestData")
    @Transactional
    void authenticateUserTraditionally_whenUserFailsValidation(LoginRequestDto loginRequestDto) throws Exception {
        //given //when //then
        mockMvc
                .perform(mvcBasicRequestBuilder.setBasicPostRequest(endPointUrl, loginRequestDto))
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