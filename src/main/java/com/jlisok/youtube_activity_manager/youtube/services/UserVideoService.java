package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import com.jlisok.youtube_activity_manager.videos.models.Video;

import java.util.List;
import java.util.UUID;

public interface UserVideoService {

    void insertVideosVideoCategoriesAndChannels(List<Video> list, Rating rating, UUID userId);

}
