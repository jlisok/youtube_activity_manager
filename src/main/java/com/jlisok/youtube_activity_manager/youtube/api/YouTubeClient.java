package com.jlisok.youtube_activity_manager.youtube.api;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.Video;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;

import java.io.IOException;
import java.util.List;

public interface YouTubeClient {

    List<Subscription> fetchSubscriptions(String accessToken, String parts) throws IOException;

    List<Channel> fetchChannels(String accessToken, String parts, List<String> channelIds) throws IOException;

    List<Video> fetchRatedVideos(String accessToken, String parts, Rating rating) throws IOException;
}
