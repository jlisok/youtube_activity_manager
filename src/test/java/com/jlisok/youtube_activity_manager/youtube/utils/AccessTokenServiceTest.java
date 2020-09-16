package com.jlisok.youtube_activity_manager.youtube.utils;

import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext.getAuthenticationInContext;
import static com.jlisok.youtube_activity_manager.security.configs.JwtAuthenticationContext.setAuthenticationInContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class AccessTokenServiceTest {

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private AccessTokenService accessTokenService;

    @MockBean
    private UserRepository repository;

    String token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjRiODNmMTgwMjNhODU1NTg3Zjk0MmU3NTEwMjI1MTEyMDg4N2Y3MjUiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2N";
    UUID userId = UUID.randomUUID();

    @BeforeEach
    public void setContext() {
        setAuthenticationInContext(token, userId);
    }

    @Test
    void getAccessToken_whenUserAndTokenValid() {
        //given
        User user = userUtils.createUser(getAuthenticationInContext().getPrincipal(), token, token);
        when(repository.findById(getAuthenticationInContext().getPrincipal()))
                .thenReturn(Optional.of(user));

        //when
        String actualToken = accessTokenService.get();

        //then
        assertEquals(token, actualToken);
    }


    @Test
    void getAccessToken_whenUserNotFound() {
        //given
        when(repository.findById(getAuthenticationInContext().getPrincipal()))
                .thenReturn(Optional.empty());

        //when //then
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> accessTokenService.get());
    }


    @Test
    void getAccessToken_whenAccessTokenEmpty() {
        //given
        User user = userUtils.createUser(getAuthenticationInContext().getPrincipal(), token, null);
        when(repository.findById(getAuthenticationInContext().getPrincipal()))
                .thenReturn(Optional.of(user));

        //when //then
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> accessTokenService.get());
    }

}