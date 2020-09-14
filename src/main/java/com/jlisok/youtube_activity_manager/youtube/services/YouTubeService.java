package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.model.ChannelListResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface YouTubeService {

    public ChannelListResponse listOfChannels(String accessToken, List<String> requestParts) throws IOException, GeneralSecurityException;

}
