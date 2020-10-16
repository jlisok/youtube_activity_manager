package com.jlisok.youtube_activity_manager.youtube.api;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoCategory;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;

import java.io.IOException;
import java.util.List;

public interface YouTubeClient {

    List<Subscription> fetchSubscriptions(String accessToken, String parts);

    List<Channel> fetchChannels(String accessToken, String parts, List<String> channelIds);

    List<Video> fetchRatedVideos(String accessToken, String parts, Rating rating);

    List<VideoCategory> fetchVideoCategories(String accessToken, String parts, List<String> categoryIds);
}
