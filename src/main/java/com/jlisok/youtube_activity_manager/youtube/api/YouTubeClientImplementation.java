package com.jlisok.youtube_activity_manager.youtube.api;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.Video;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class YouTubeClientImplementation implements YouTubeClient {

    private final YouTubeBuilder youTubeBuilder;

    @Autowired
    public YouTubeClientImplementation(YouTubeBuilder youTubeBuilder) {
        this.youTubeBuilder = youTubeBuilder;
    }

    @Override
    public List<Subscription> fetchSubscribedChannels(String token, String parts) throws IOException {
        YouTube youTube = youTubeBuilder.get(token);
        return youTube
                .subscriptions()
                .list(parts)
                .setMine(true)
                .execute()
                .getItems();
    }


    @Override
    public List<Video> fetchRatedVideos(String token, String parts, Rating rating) throws IOException {
        YouTube youTube = youTubeBuilder.get(token);
        return youTube
                .videos()
                .list(parts)
                .setMyRating(rating.toString().toLowerCase())
                .execute()
                .getItems();
    }

}
