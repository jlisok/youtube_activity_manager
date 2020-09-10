package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.model.ChannelListResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class YouTubeServiceImplementationTest {

    @Autowired
    YouTubeServiceImplementation service;

    @Test
    void listLikedChannels() throws IOException, GeneralSecurityException {
        List<String> requestParts = Arrays.asList("snippet", "contentDetails", "statistics");
        ChannelListResponse response = service.listLikedChannels("UC_x5XG1OV2P6uZZ5FSM9Ttw", requestParts);
    }
}