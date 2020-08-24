package com.jlisok.youtube_activity_manager.login.services;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jlisok.youtube_activity_manager.login.dto.LoginRequestDto;
import com.jlisok.youtube_activity_manager.registration.dto.RegistrationRequestDto;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.testutils.RandomRegistrationDto;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.FailedLoginException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class LoginServiceTest {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTVerifier jwtVerifier;

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
    void authenticateUser_whenUserIsPresentInDatabaseAndLoginDataAreValid() throws RegistrationException, FailedLoginException {
        //given
        User user = UserUtils.createUserInDatabase(validRegistrationRequest);
        LoginRequestDto validLoginRequestDto = new LoginRequestDto(userPassword, userEmail);

        assertTrue(userRepository
                .findByEmail(user.getEmail())
                .isPresent());

        //when
        String token = loginService.authenticateUser(validLoginRequestDto);
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        //then
        Assertions.assertNotNull(token);
        Assertions.assertEquals(user.getId().toString(), decodedJWT.getSubject());
    }


    @Test
    @Transactional
    void authenticateUser_whenUserIsNotPresentInDatabase() {
        //given
        LoginRequestDto loginRequestDtoUserNotPresentInDatabase = new LoginRequestDto(userPassword, userEmail);

        //when //then
        Assertions.assertThrows(FailedLoginException.class, () -> loginService.authenticateUser(loginRequestDtoUserNotPresentInDatabase));
    }


    @Test
    @Transactional
    void authenticateUser_whenUserIsPresentInDatabaseAndBadPassword() throws RegistrationException {
        //given
        User user = UserUtils.createUserInDatabase(validRegistrationRequest);
        assertTrue(userRepository
                .findByEmail(user.getEmail())
                .isPresent());


        LoginRequestDto loginRequestDtoBadPassword = new LoginRequestDto("some_other_password", userEmail);

        //when //then
        Assertions.assertThrows(FailedLoginException.class, () -> loginService.authenticateUser(loginRequestDtoBadPassword));
    }


}