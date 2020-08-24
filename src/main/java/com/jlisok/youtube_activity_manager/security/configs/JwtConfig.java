package com.jlisok.youtube_activity_manager.security.configs;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.jlisok.youtube_activity_manager.security.configs.JwtSecurityConstants.SECRET;

@Configuration
public class JwtConfig {

    @Bean
    public Algorithm jwtAlgorithm() {
        return HMAC512(SECRET.getBytes());
    }

    @Bean
    public JWTVerifier jwtVerifier(Algorithm jwtAlgorithm) {
        return JWT.require(jwtAlgorithm).build();
    }


}
