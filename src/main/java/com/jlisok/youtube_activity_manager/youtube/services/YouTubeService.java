package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.model.Subscription;
import com.jlisok.youtube_activity_manager.database.exceptions.ExpectedDataNotFoundInDatabase;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.youtube.dto.YouTubeRatingDto;

import java.io.IOException;
import java.util.List;

public interface YouTubeService {

    List<Subscription> listSubscribedChannels() throws IOException;

    List<Video> listRatedVideos(YouTubeRatingDto dto) throws IOException, ExpectedDataNotFoundInDatabase;

}
