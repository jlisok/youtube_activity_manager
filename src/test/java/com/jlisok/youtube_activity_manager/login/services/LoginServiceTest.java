package com.jlisok.youtube_activity_manager.login.services;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jlisok.youtube_activity_manager.login.dto.LoginRequestDto;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.security.auth.login.FailedLoginException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class LoginServiceTest {

    @Autowired
    private JWTVerifier jwtVerifier;

    @Autowired
    private UserUtils userUtils;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private LoginService loginService;

    private String userEmail;
    private String userPassword;
    private LoginRequestDto dto;

    @BeforeEach
    void createRandomUser() {
        userEmail = userUtils.createRandomEmail();
        userPassword = userUtils.createRandomPassword();
        dto = new LoginRequestDto(userPassword, userEmail);
    }



    @Test
    void authenticateUser_whenUserIsPresentInDatabaseAndLoginDataAreValid() throws RegistrationException, FailedLoginException {
        //given
        User user = userUtils.createUser(userEmail, userPassword);

        when(userRepository.findByEmail(any(String.class)))
                .thenReturn(Optional.of(user));

        //when
        String token = loginService.authenticateUser(dto);
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        //then
        Assertions.assertNotNull(token);
        Assertions.assertEquals(user.getId().toString(), decodedJWT.getSubject());
    }





    @Test
    void authenticateUser_whenUserIsNotPresentInDatabase() {
        //given

        //when //then
        Assertions.assertThrows(FailedLoginException.class, () -> loginService.authenticateUser(dto));
    }





    @Test
    void authenticateUser_whenUserIsPresentInDatabaseAndBadPassword() throws RegistrationException {
        //given
        User user = userUtils.createUser(userEmail, userPassword);

        when(userRepository.findByEmail(any(String.class)))
                .thenReturn(Optional.of(user));

        LoginRequestDto loginRequestDtoBadPassword = new LoginRequestDto("some_other_password", userEmail);

        //when //then
        Assertions.assertThrows(FailedLoginException.class, () -> loginService.authenticateUser(loginRequestDtoBadPassword));
    }


}