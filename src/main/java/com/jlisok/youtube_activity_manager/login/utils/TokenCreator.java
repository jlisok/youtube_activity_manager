package com.jlisok.youtube_activity_manager.login.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

import static com.jlisok.youtube_activity_manager.security.configs.JwtSecurityConstants.DURATION;
import static com.jlisok.youtube_activity_manager.security.configs.JwtSecurityConstants.ISSUER;

@Component
public class TokenCreator {

    public String create(String subject, Algorithm algorithm, Instant now) {
        Instant expirationTime = now.plus(DURATION);
        return JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(Date.from(now))
                .withSubject(subject)
                .withExpiresAt(Date.from(expirationTime))
                .sign(algorithm);
    }

}
