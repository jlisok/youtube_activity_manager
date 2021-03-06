package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.database.exceptions.ExpectedDataNotFoundInDatabase;
import com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory;
import com.jlisok.youtube_activity_manager.videos.models.Video;

import java.util.List;

public interface VideoService {

    List<Video> createVideos(List<com.google.api.services.youtube.model.Video> youTubeVideos, List<Channel> channels, List<VideoCategory> videoCategories) throws ExpectedDataNotFoundInDatabase;

}
