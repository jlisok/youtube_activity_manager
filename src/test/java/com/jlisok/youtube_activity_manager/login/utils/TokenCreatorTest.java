package com.jlisok.youtube_activity_manager.login.utils;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.jlisok.youtube_activity_manager.security.configs.JwtSecurityConstants.DURATION;
import static com.jlisok.youtube_activity_manager.security.configs.JwtSecurityConstants.ISSUER;

@SpringBootTest
class TokenCreatorTest {

    @Autowired
    private Algorithm jwtAlgorithm;

    @Autowired
    private JWTVerifier jwtVerifier;

    @Autowired
    private TokenCreator tokenCreator;

    @Test
    void createToken_whenClaimsAreValid() {
        //given
        Instant expectedIssuedAt = Instant.now();
        String expectedUserId = UUID.randomUUID().toString();
        Instant expectedExpirationTime = expectedIssuedAt.plus(DURATION);

        //when
        String token = tokenCreator.create(expectedUserId, jwtAlgorithm, expectedIssuedAt);
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        //then
        Assertions.assertEquals(expectedUserId, decodedJWT.getSubject());
        Assertions.assertEquals(expectedIssuedAt.truncatedTo(ChronoUnit.SECONDS), decodedJWT.getIssuedAt().toInstant().truncatedTo(ChronoUnit.SECONDS));
        Assertions.assertEquals(expectedExpirationTime.truncatedTo(ChronoUnit.SECONDS), decodedJWT.getExpiresAt().toInstant().truncatedTo(ChronoUnit.SECONDS));
        Assertions.assertEquals(ISSUER, decodedJWT.getIssuer());
    }

}