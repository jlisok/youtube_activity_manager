package com.jlisok.youtube_activity_manager.testutils;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlisok.youtube_activity_manager.domain.exceptions.CustomErrorResponse;
import com.jlisok.youtube_activity_manager.domain.exceptions.ResponseCode;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component
public class MockMvcResultTester {

    private final ObjectMapper objectMapper;
    private final JWTVerifier jwtVerifier;
    private final UserRepository repository;

    @Autowired
    public MockMvcResultTester(ObjectMapper objectMapper, JWTVerifier jwtVerifier, UserRepository repository) {
        this.objectMapper = objectMapper;
        this.jwtVerifier = jwtVerifier;
        this.repository = repository;
    }


    public void assertEqualsResponseCode(ResponseCode expected, MvcResult result) throws Exception {
        ResponseCode actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CustomErrorResponse.class)
                .getResponseCode();

        assertEquals(expected, actual);
    }


    public void assertEqualsTokenSubject(String expected, MvcResult result) throws Exception {
        DecodedJWT decodedJWT = jwtVerifier.verify(result.getResponse().getContentAsString());
        assertEquals(expected, decodedJWT.getSubject());
    }


    public void assertEqualsTokenSubjectAndGoogleIdNotNull(String email, MvcResult result) throws Exception {
        User user = repository
                .findByEmail(email)
                .get();

        DecodedJWT decodedJWT = jwtVerifier.verify(result.getResponse().getContentAsString());
        assertEquals(user.getId().toString(), decodedJWT.getSubject());
        assertNotNull(user.getGoogleId());
    }

}
