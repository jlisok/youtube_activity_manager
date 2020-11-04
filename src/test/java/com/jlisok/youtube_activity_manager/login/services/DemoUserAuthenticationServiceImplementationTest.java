package com.jlisok.youtube_activity_manager.login.services;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jlisok.youtube_activity_manager.login.exceptions.DemoUserNotFound;
import com.jlisok.youtube_activity_manager.login.utils.JwtClaimNames;
import com.jlisok.youtube_activity_manager.testutils.UserTestUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
class DemoUserAuthenticationServiceImplementationTest {

    @Value("${demo.user_email}")
    private String userEmail;

    @Autowired
    private DemoUserAuthenticationService service;

    @Autowired
    private UserTestUtils userTestUtils;

    @Autowired
    private JWTVerifier jwtVerifier;

    @MockBean
    private UserRepository repository;

    private User user;

    @BeforeEach
    void createInitialConditions() {
        user = userTestUtils.createUserAndSetDummyGoogleTokens(userEmail);
    }


    @Test
    void authenticateUser_whenDemoUserPresent() {
        //given
        when(repository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        // when
        var token = service.authenticateUser();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        //then
        Assertions.assertNotNull(token);
        Assertions.assertTrue(decodedJWT.getClaim(JwtClaimNames.AUTHORIZED).asBoolean());
        Assertions.assertEquals(user.getId().toString(), decodedJWT.getSubject());
    }


    @Test
    void authenticateUser_whenDemoUserNotPresent() {
        //given
        when(repository.findByEmail(userEmail))
                .thenReturn(Optional.empty());

        // when //then
        Assertions.assertThrows(DemoUserNotFound.class, () -> service.authenticateUser());
    }

}