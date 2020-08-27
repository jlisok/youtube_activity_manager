package com.jlisok.youtube_activity_manager.login.utils;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@SpringBootTest
class TokenCreatorTest {

    @Autowired
    private JWTVerifier jwtVerifier;

    @Autowired
    private TokenCreator tokenCreator;

    @Value("${security.config.constants.token.duration}")
    private Duration duration;

    @Value("${security.config.constants.issuer}")
    private String issuer;

    @Test
    void createToken_whenClaimsAreValid() {
        //given
        Instant expectedIssuedAt = Instant.now();
        String expectedUserId = UUID.randomUUID().toString();
        Instant expectedExpirationTime = expectedIssuedAt.plus(duration);

        //when
        String token = tokenCreator.create(expectedUserId, expectedIssuedAt);
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        //then
        Assertions.assertEquals(expectedUserId, decodedJWT.getSubject());

        //Method decodedJWT.getIssuedAt().toInstant() returns Instant with a precision set to seconds thus expected value must be truncated
        Assertions.assertEquals(expectedIssuedAt.truncatedTo(ChronoUnit.SECONDS), decodedJWT.getIssuedAt().toInstant());
        Assertions.assertEquals(expectedExpirationTime.truncatedTo(ChronoUnit.SECONDS), decodedJWT.getExpiresAt().toInstant());
        Assertions.assertEquals(issuer, decodedJWT.getIssuer());
    }


}