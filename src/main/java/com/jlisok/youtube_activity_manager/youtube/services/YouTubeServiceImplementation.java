package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
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
    public ChannelListResponse listLikedChannels(String userId, List<String> requestParts) throws IOException, GeneralSecurityException {
        YouTube youTube = youTubeApi.get(userId);
        return youTube
                .channels()
                .list(String.join(",", requestParts))
                .setId(userId)
                .execute();
    }
}
