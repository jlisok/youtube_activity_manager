package com.jlisok.youtube_activity_manager.testutils;

import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MockMvcYouTubeListResponse {

    public static void assertResponseListNotNull(MvcResult result) throws Exception {
        String response = result
                .getResponse()
                .getContentAsString();

        assertNotNull(response);
        assertTrue(response.contains("snippet"));
        assertTrue(response.contains("contentDetails"));
    }
}
