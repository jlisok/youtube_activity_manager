package com.jlisok.youtube_activity_manager.youtube.api;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.youtube.utils.YouTubeActivityGetter;
import com.jlisok.youtube_activity_manager.youtube.utils.YouTubeApiRequestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class YouTubeClientImplementation implements YouTubeClient {

    private final YouTubeBuilder youTubeBuilder;
    private final YouTubeActivityGetter getter;


    @Autowired
    public YouTubeClientImplementation(YouTubeBuilder youTubeBuilder, YouTubeActivityGetter getter) {
        this.youTubeBuilder = youTubeBuilder;
        this.getter = getter;
    }


    @Override
    public List<Subscription> fetchSubscriptions(String accessToken, String parts) {
        YouTube youTube = youTubeBuilder.get(accessToken);
        List<Subscription> subscriptions = new ArrayList<>();
        String pageToken = "";
        do {
            SubscriptionListResponse response = getter.getYouTubeSubscriptions(youTube, parts, pageToken);
            subscriptions.addAll(response.getItems());
            pageToken = response.getNextPageToken();
        }
        while (pageToken != null && !pageToken.isEmpty());
        return subscriptions;
    }


    @Override
    public List<Channel> fetchChannels(String accessToken, String parts, List<String> channelIds) {
        YouTube youTube = youTubeBuilder.get(accessToken);
        List<String> inputChannelIds = YouTubeApiRequestHelper.separateIdsIntoMaxRequestCapacity(channelIds);
        return inputChannelIds
                .stream()
                .map(inputIds -> getter.getYouTubeChannels(youTube, parts, inputIds))
                .flatMap(items -> items.getItems().stream())
                .collect(Collectors.toList());
    }


    @Override
    public List<Video> fetchRatedVideos(String accessToken, String parts, Rating rating) {
        YouTube youTube = youTubeBuilder.get(accessToken);
        List<Video> videos = new ArrayList<>();
        String pageToken = "";
        do {
            VideoListResponse response = getter.getYouTubeVideos(youTube, parts, rating, pageToken);
            videos.addAll(response.getItems());
            pageToken = response.getNextPageToken();
        }
        while (pageToken != null && !pageToken.isEmpty());
        return videos;
    }

    @Override
    public List<VideoCategory> fetchVideoCategories(String accessToken, String parts, List<String> categoryIds) {
        YouTube youTube = youTubeBuilder.get(accessToken);
        List<String> inputCategoryIds = YouTubeApiRequestHelper.separateIdsIntoMaxRequestCapacity(categoryIds);
        return inputCategoryIds
                .stream()
                .map(inputIds -> getter.getYouTubeVideoCategories(youTube, parts, inputIds))
                .flatMap(items -> items.getItems().stream())
                .collect(Collectors.toList());
    }
}
