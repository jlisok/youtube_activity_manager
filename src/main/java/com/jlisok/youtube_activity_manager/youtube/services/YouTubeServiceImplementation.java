package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.Video;
import com.jlisok.youtube_activity_manager.youtube.api.YouTubeApi;
import com.jlisok.youtube_activity_manager.youtube.dto.YouTubeListDto;
import com.jlisok.youtube_activity_manager.youtube.utils.AccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
public class YouTubeServiceImplementation implements YouTubeService {

    private final YouTubeApi youTubeApi;
    private final AccessTokenService token;

    @Autowired
    public YouTubeServiceImplementation(YouTubeApi youTubeApi, AccessTokenService token) {
        this.youTubeApi = youTubeApi;
        this.token = token;
    }


    @Override
    public List<Subscription> listOfChannels(YouTubeListDto dto) throws IOException, GeneralSecurityException {
        YouTube youTube = youTubeApi.get(token.get());
        return youTube
                .subscriptions()
                .list(String.join(",", dto.getRequestParts()))
                .setMine(true)
                .execute()
                .getItems();
    }


    @Override
    public List<Video> listRatedVideos(YouTubeListDto dto) throws IOException, GeneralSecurityException {
        YouTube youTube = youTubeApi.get(token.get());
        return youTube
                .videos()
                .list(String.join(",", dto.getRequestParts()))
                .setMyRating(dto.getRating())
                .execute()
                .getItems();
    }


}


