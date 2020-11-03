package com.jlisok.youtube_activity_manager.login.services;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jlisok.youtube_activity_manager.login.dto.LoginRequestDto;
import com.jlisok.youtube_activity_manager.login.utils.JwtClaimNames;
import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.testutils.TestProfile;
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

import static org.mockito.Mockito.when;

@SpringBootTest
class TraditionalLoginServiceTest implements TestProfile {

    @Autowired
    private JWTVerifier jwtVerifier;

    @Autowired
    private UserUtils userUtils;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private TraditionalLoginService traditionalLoginService;

    private final String dummyGoogleIdToken = "dummyGoogleIdTokenDummyGoogleIdTokenDummyGoogleIdTokenDummyGoogleIdToken";

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
    void authenticateUser_whenUserPresentAndLoginDataValidAndEverAuthorizedTrue() throws RegistrationException, FailedLoginException {
        //given
        User user = userUtils.createUser(userEmail, userPassword);
        user.setGoogleIdToken(dummyGoogleIdToken);

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        //when
        String token = traditionalLoginService.authenticateUser(dto);

        //then
        Assertions.assertNotNull(token);
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        Assertions.assertEquals(user.checkIfEverAuthorized(), decodedJWT.getClaim(JwtClaimNames.AUTHORIZED).asBoolean());
        Assertions.assertEquals(user.getId().toString(), decodedJWT.getSubject());
    }


    @Test
    void authenticateUser_whenUserPresentAndLoginDataValidAndEverAuthorizedFalse() throws RegistrationException, FailedLoginException {
        //given
        User user = userUtils.createUser(userEmail, userPassword);

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        //when
        String token = traditionalLoginService.authenticateUser(dto);

        //then
        Assertions.assertNotNull(token);
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        Assertions.assertEquals(user.checkIfEverAuthorized(), decodedJWT.getClaim(JwtClaimNames.AUTHORIZED).asBoolean());
        Assertions.assertEquals(user.getId().toString(), decodedJWT.getSubject());
    }


    @Test
    void authenticateUser_whenUserIsNotPresentInDatabase() {
        //given

        //when //then
        Assertions.assertThrows(FailedLoginException.class, () -> traditionalLoginService.authenticateUser(dto));
    }


    @Test
    void authenticateUser_whenUserIsPresentInDatabaseAndBadPassword() throws RegistrationException {
        //given
        User user = userUtils.createUser(userEmail, userPassword);

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        LoginRequestDto loginRequestDtoBadPassword = new LoginRequestDto("some_other_password", userEmail);

        //when //then
        Assertions.assertThrows(FailedLoginException.class, () -> traditionalLoginService.authenticateUser(loginRequestDtoBadPassword));
    }


}