package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.Video;

import java.util.List;
import java.util.Set;

public interface YouTubeChannelIdService {

    List<String> getChannelIdFromSubscriptions(List<Subscription> subscriptions);

    Set<String> getChannelIdFromVideos(List<Video> videos);

}
