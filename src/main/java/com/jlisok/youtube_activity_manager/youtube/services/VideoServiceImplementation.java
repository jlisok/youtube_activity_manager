package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.youtube.utils.EntityCreator;
import com.jlisok.youtube_activity_manager.youtube.utils.VideoDescription;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoServiceImplementation implements VideoService {


    @Override
    public List<Video> createVideos(List<com.google.api.services.youtube.model.Video> youTubeVideoList) {
        return youTubeVideoList
                .stream()
                .map(this::translateToVideo)
                .collect(Collectors.toList());
    }


    private Video translateToVideo(com.google.api.services.youtube.model.Video youtubeVideo) {
        List<String> uriList = VideoDescription.toListOfUri(youtubeVideo.getSnippet().getDescription());
        return EntityCreator.createVideo(youtubeVideo.getId(), youtubeVideo.getSnippet(), youtubeVideo.getContentDetails(), uriList);
    }
}
