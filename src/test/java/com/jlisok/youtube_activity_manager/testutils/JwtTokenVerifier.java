package com.jlisok.youtube_activity_manager.testutils;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@Component
public class JwtTokenVerifier {

    private final JWTVerifier jwtVerifier;
    private final UserRepository repository;

    @Autowired
    public JwtTokenVerifier(JWTVerifier jwtVerifier, UserRepository repository) {
        this.jwtVerifier = jwtVerifier;
        this.repository = repository;
    }


    public void assertEqualsTokenSubject(String expected, MvcResult result) throws Exception {
        DecodedJWT decodedJWT = jwtVerifier.verify(result
                .getResponse()
                .getContentAsString());
        assertEquals(expected, decodedJWT.getSubject());
    }


    public void assertEqualsUserIdsAndGoogleIdAndTokenNotNull(String email, MvcResult result) throws Exception {
        User user = repository
                .findByEmail(email)
                .get();

        DecodedJWT decodedJWT = jwtVerifier.verify(result
                .getResponse()
                .getContentAsString());
        assertEquals(user
                .getId()
                .toString(), decodedJWT.getSubject());
        assertNotNull(user.getGoogleId());
        assertNotNull(user.getAccessToken());
    }
}
