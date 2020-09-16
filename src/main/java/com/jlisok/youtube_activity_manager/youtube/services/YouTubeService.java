package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.Video;
import com.jlisok.youtube_activity_manager.youtube.dto.YouTubeListDto;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface YouTubeService {

    List<Subscription> listOfChannels(YouTubeListDto dto) throws IOException, GeneralSecurityException;

    List<Video> listRatedVideos(YouTubeListDto dto) throws IOException, GeneralSecurityException;

}
