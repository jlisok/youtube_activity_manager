package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.database.exceptions.ExpectedDataNotFoundInDatabase;
import com.jlisok.youtube_activity_manager.videos.models.Video;

import java.util.List;

public interface VideoService {

    List<Video> createListOfVideos(List<com.google.api.services.youtube.model.Video> videoList) throws ExpectedDataNotFoundInDatabase;

}
