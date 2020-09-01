package com.jlisok.youtube_activity_manager.testutils;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlisok.youtube_activity_manager.domain.exceptions.CustomErrorResponse;
import com.jlisok.youtube_activity_manager.domain.exceptions.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Component
public class MockMvcResultTester {

    private final ObjectMapper objectMapper;
    private final JWTVerifier jwtVerifier;

    @Autowired
    public MockMvcResultTester(ObjectMapper objectMapper, JWTVerifier jwtVerifier) {
        this.objectMapper = objectMapper;
        this.jwtVerifier = jwtVerifier;
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

}
