package com.jlisok.youtube_activity_manager.testutils;

import com.jlisok.youtube_activity_manager.login.utils.TokenCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static com.jlisok.youtube_activity_manager.security.configs.JwtSecurityConstants.HEADER_START_SCHEMA;

@Component
public class AuthenticationUtils {

    @Autowired
    private TokenCreator creator;

    public String createRequestAuthenticationHeader(String userId, boolean ifEverAuthorized) {
        Instant now = Instant.now();
        String token = creator.create(userId, now, ifEverAuthorized);
        return HEADER_START_SCHEMA + token;
    }
}
