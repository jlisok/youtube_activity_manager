package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
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
    void listOfChannels() throws IOException, GeneralSecurityException {
        List<String> requestParts = Arrays.asList("snippet", "contentDetails", "statistics");
        String accessToken = "ya29.a0AfH6SMB1Hfy-mxIBUM5VDKRlLqFSfUMoCsR6lCytSkf-qE06Yejx8PVYeOFKCN8kRxFZL8sXBINoNH7zSLkR7ED7LHZw_QO4ma5x5CduqTO-Lx0qjXqvA7K0iyDhTpzMn_s7z8ZqFKewAvwRp-l55tuJWI10H0d0ZtQ";
        List<Channel> response = service.listOfChannels(accessToken, requestParts);
        System.out.println(response);
    }


    @Test
    void listLikedVideos() throws IOException, GeneralSecurityException {
        List<String> requestParts = Arrays.asList("snippet", "contentDetails", "statistics");
        String accessToken = "ya29.a0AfH6SMB1Hfy-mxIBUM5VDKRlLqFSfUMoCsR6lCytSkf-qE06Yejx8PVYeOFKCN8kRxFZL8sXBINoNH7zSLkR7ED7LHZw_QO4ma5x5CduqTO-Lx0qjXqvA7K0iyDhTpzMn_s7z8ZqFKewAvwRp-l55tuJWI10H0d0ZtQ";
        List<Video> response = service.listRatedVideos(accessToken, "like", requestParts);
        System.out.println(response);
    }
}