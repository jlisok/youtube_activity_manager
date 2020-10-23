package com.jlisok.youtube_activity_manager.youtube.api;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoCategory;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface YouTubeClient {

    List<Subscription> fetchSubscriptions(String accessToken, String parts, UUID userId);

    List<Channel> fetchChannels(String accessToken, String parts, List<String> channelIds, UUID userId);

    List<Video> fetchRatedVideos(String accessToken, String parts, Rating rating, UUID userId);

    List<VideoCategory> fetchVideoCategories(String accessToken, String parts, List<String> categoryIds, UUID userId);
}
