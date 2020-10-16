package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory;
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
    public List<Video> createVideos(List<com.google.api.services.youtube.model.Video> youTubeVideos, List<Channel> channels, List<VideoCategory> videoCategories) {
        Map<String, Channel> channelMap = MapCreator.toMap(channels, Channel::getYouTubeChannelId, Function.identity());
        Map<String, VideoCategory> categoryMap = MapCreator.toMap(videoCategories, VideoCategory::getYoutubeId, Function
                .identity());

        return youTubeVideos
                .stream()
                .map(video -> {
                    var channel = channelMap.get(video.getSnippet().getChannelId());
                    var videoCategory = categoryMap.get(video.getSnippet().getCategoryId());
                    return translateToVideo(video, channel, videoCategory);
                })
                .collect(Collectors.toList());
    }


    private Video translateToVideo(com.google.api.services.youtube.model.Video youtubeVideo, Channel channel, VideoCategory videoCategory) {
        List<String> uriList = VideoDescription.toListOfUri(youtubeVideo.getSnippet().getDescription());
        return EntityCreator.createVideo(youtubeVideo.getId(), youtubeVideo.getSnippet(), youtubeVideo.getContentDetails(), uriList, channel, videoCategory);
    }
}
