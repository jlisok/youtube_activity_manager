package com.jlisok.youtube_activity_manager.youtube.api;

import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.Video;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;

import java.util.List;

public interface YouTubeClient {

    List<Subscription> fetchSubscribedChannels(String token, String parts) throws Exception;
    List<Video> fetchRatedVideos(String token, String parts, Rating rating) throws Exception;


}
