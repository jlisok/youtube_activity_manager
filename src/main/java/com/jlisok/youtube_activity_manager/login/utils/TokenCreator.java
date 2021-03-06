package com.jlisok.youtube_activity_manager.login.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class TokenCreator {

    @Value("${security.config.constants.token.duration}")
    private Duration duration;

    @Value("${security.config.constants.issuer}")
    private String issuer;

    private final Algorithm algorithm;

    @Autowired
    public TokenCreator(Algorithm algorithm) {
        this.algorithm = algorithm;
    }


    public String create(String subject, Instant now, Boolean ifEverAuthorized) {
        Instant expirationTime = now.plus(duration);
        return JWT.create()
                  .withIssuer(issuer)
                  .withIssuedAt(Date.from(now))
                  .withSubject(subject)
                  .withClaim(JwtClaimNames.AUTHORIZED, ifEverAuthorized)
                  .withExpiresAt(Date.from(expirationTime))
                  .sign(algorithm);
    }
}
