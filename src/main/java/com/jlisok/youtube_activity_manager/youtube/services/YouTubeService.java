package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.model.Subscription;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.youtube.dto.YouTubeRatingDto;

import java.util.List;

public interface YouTubeService {

    List<Subscription> listSubscribedChannels() throws Exception;

    List<Video> listRatedVideos(YouTubeRatingDto dto) throws Exception;

}
