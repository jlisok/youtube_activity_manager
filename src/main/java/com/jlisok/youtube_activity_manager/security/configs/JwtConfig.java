package com.jlisok.youtube_activity_manager.security.configs;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Configuration
public class JwtConfig {

    @Value("${security.config.constants.secret}")
    private String secret;

    @Bean
    public Algorithm jwtAlgorithm() {
        return HMAC512(secret.getBytes());
    }

    @Bean
    public JWTVerifier jwtVerifier(Algorithm jwtAlgorithm) {
        return JWT.require(jwtAlgorithm).build();
    }

}
