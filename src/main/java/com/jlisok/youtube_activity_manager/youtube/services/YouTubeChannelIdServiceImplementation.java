package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.Video;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class YouTubeChannelIdServiceImplementation implements YouTubeChannelIdService {

    @Override
    public List<String> getChannelIdFromSubscriptions(List<Subscription> subscriptionList) {
        return subscriptionList.stream()
                               .map(subscription -> subscription.getSnippet().getResourceId().getChannelId())
                               .collect(Collectors.toList());
    }

    @Override
    public List<String> getChannelIdFromVideos(List<Video> videos) {
        return videos.stream()
                     .map(video -> video.getSnippet().getChannelId())
                     .distinct()
                     .collect(Collectors.toList());
    }
}
