package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.videos.enums.Rating;

import java.util.UUID;

public interface YouTubeService {

    void synchronizeSubscribedChannels(String accessToken, UUID userId);

    void synchronizeRatedVideos(String accessToken, UUID userId, Rating rating);

}
