package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.jlisok.youtube_activity_manager.youtube.api.YouTubeApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Component
public class YouTubeServiceImplementation implements YouTubeService {

    private final YouTubeApi youTubeApi;

    @Autowired
    public YouTubeServiceImplementation(YouTubeApi youTubeApi) {
        this.youTubeApi = youTubeApi;
    }

    @Override
    public ChannelListResponse listOfChannels(String accessToken, List<String> requestParts) throws IOException, GeneralSecurityException {
        YouTube youTube = youTubeApi.get(accessToken);
        return youTube
                .channels()
                .list(String.join(",", requestParts))
                .setMine(true)
                .execute();
    }


    public VideoListResponse listOfVideos(String accessToken, String rating, List<String> requestParts) throws IOException, GeneralSecurityException {
        YouTube youTube = youTubeApi.get(accessToken);
        return youTube
                .videos()
                .list(String.join(",", requestParts))
                .setMyRating(rating)
                .execute();
    }
}
