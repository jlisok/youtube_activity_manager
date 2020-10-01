package com.jlisok.youtube_activity_manager.testutils;

import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YouTubeListVerifier {

    public static void assertResponseListNotNull(MvcResult result) throws Exception {
        String response = result
                .getResponse()
                .getContentAsString();

        assertNotNull(response);
        assertTrue(response.contains("id"));
        assertTrue(response.contains("title"));
        assertTrue(response.contains("channelId"));
        assertTrue(response.contains("duration"));
        assertTrue(response.contains("hashtag"));
        assertTrue(response.contains("uri"));
    }


}
