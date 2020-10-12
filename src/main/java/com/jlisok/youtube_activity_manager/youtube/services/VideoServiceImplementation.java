package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channel.models.Channel;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.youtube.utils.EntityCreator;
import com.jlisok.youtube_activity_manager.youtube.utils.MapCreator;
import com.jlisok.youtube_activity_manager.youtube.utils.VideoDescription;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class VideoServiceImplementation implements VideoService {

    @Override
    public List<Video> createVideos(List<com.google.api.services.youtube.model.Video> youTubeVideos, List<Channel> channels) {
        Map<String, Channel> channelMap = MapCreator.toMap(channels, Channel::getYouTubeChannelId, Function.identity());
        return youTubeVideos
                .stream()
                .map(video -> {
                    Channel channel = channelMap.get(video.getSnippet().getChannelId());
                    return translateToVideo(video, channel);
                })
                .collect(Collectors.toList());
    }


    private Video translateToVideo(com.google.api.services.youtube.model.Video youtubeVideo, Channel channel) {
        List<String> uriList = VideoDescription.toListOfUri(youtubeVideo.getSnippet().getDescription());
        return EntityCreator.createVideo(youtubeVideo.getId(), youtubeVideo.getSnippet(), youtubeVideo.getContentDetails(), uriList, channel);
    }
}
