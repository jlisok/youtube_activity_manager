package com.jlisok.youtube_activity_manager.testutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@Component
public class MockMvcBasicRequestBuilder {

    @Autowired
    private ObjectMapper objectMapper;

    public MockHttpServletRequestBuilder setBasicGetRequest(String endPointUrl, String jsonHeader) {
        return MockMvcRequestBuilders
                .get(endPointUrl)
                .header(HttpHeaders.AUTHORIZATION, jsonHeader);
    }


    public MockHttpServletRequestBuilder setGetRequestWithParams(String endPointUrl, String jsonHeader, String parameterName, String parameterValue) {
        return MockMvcRequestBuilders
                .get(endPointUrl)
                .header(HttpHeaders.AUTHORIZATION, jsonHeader)
                .param(parameterName, parameterValue);
    }


    public MockHttpServletRequestBuilder setBasicPostRequest(String endPointUrl, Object dto) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .post(endPointUrl)
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON);
    }


    public MockHttpServletRequestBuilder setPostRequestWithAuthorizationHeader(String endPointUrl, String jsonHeader, Object dto) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .post(endPointUrl)
                .header(HttpHeaders.AUTHORIZATION, jsonHeader)
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON);
    }

}
