package com.jlisok.youtube_activity_manager.youtube.api;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
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
    private final Long maxResultsPerPage = 50L;

    @Autowired
    public YouTubeClientImplementation(YouTubeBuilder youTubeBuilder) {
        this.youTubeBuilder = youTubeBuilder;
    }


    //TODO: retrieving full list of videos/channels, for now only 50 first results are being retrieved
    //TODO: fetching list of channels not existing in database, when fetching list of videos! Crucial functionality!
    @Override
    public List<Subscription> fetchSubscriptions(String accessToken, String parts) throws IOException {
        YouTube youTube = youTubeBuilder.get(accessToken);
        return youTube
                .subscriptions()
                .list(parts)
                .setMine(true)
                .setMaxResults(maxResultsPerPage)
                .execute()
                .getItems();
    }


    //TODO: retrieving full list of videos/channels, for now only 50 first results are being retrieved
    @Override
    public List<Channel> fetchChannels(String accessToken, String parts, List<String> channelIds) throws IOException {
        YouTube youTube = youTubeBuilder.get(accessToken);
        String inputIds = String.join(",", channelIds);
        return youTube
                .channels()
                .list(parts)
                .setId(inputIds)
                .setMaxResults(maxResultsPerPage)
                .execute()
                .getItems();
    }


    //TODO: retrieving full list of videos/channels, for now only 50 first results are being retrieved
    @Override
    public List<Video> fetchRatedVideos(String accessToken, String parts, Rating rating) throws IOException {
        YouTube youTube = youTubeBuilder.get(accessToken);
        return youTube
                .videos()
                .list(parts)
                .setMyRating(rating.toString().toLowerCase())
                .setMaxResults(maxResultsPerPage)
                .execute()
                .getItems();
    }
}
