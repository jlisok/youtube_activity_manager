package com.jlisok.youtube_activity_manager.testutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlisok.youtube_activity_manager.domain.exceptions.CustomErrorResponse;
import com.jlisok.youtube_activity_manager.domain.exceptions.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Component
public class MvcResponseVerifier {

    private final ObjectMapper objectMapper;

    @Autowired
    public MvcResponseVerifier(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public void assertEqualsResponseCode(ResponseCode expected, MvcResult result) throws Exception {
        ResponseCode actual = objectMapper
                .readValue(result
                                   .getResponse()
                                   .getContentAsString(), CustomErrorResponse.class)
                .getResponseCode();

        assertEquals(expected, actual);
    }

}
