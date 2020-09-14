package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface YouTubeService {

    List<Channel> listOfChannels(List<String> requestParts) throws IOException, GeneralSecurityException;

    List<Video> listRatedVideos(String rating, List<String> requestParts) throws IOException, GeneralSecurityException;

}
