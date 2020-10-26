package com.jlisok.youtube_activity_manager.youtube.services;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.videos.repositories.VideoRepository;
import com.jlisok.youtube_activity_manager.youtube.utils.EntityCreator;
import com.jlisok.youtube_activity_manager.youtube.utils.IdsFetcher;
import com.jlisok.youtube_activity_manager.youtube.utils.MapCreator;
import com.jlisok.youtube_activity_manager.youtube.utils.VideoDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VideoServiceImplementation implements VideoService {

    private final VideoRepository repository;
    private final EntityManager entityManager;

    @Autowired
    public VideoServiceImplementation(VideoRepository repository, EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @Override
    public List<Video> createVideos(List<com.google.api.services.youtube.model.Video> youTubeVideos, List<Channel> dbChannels, List<VideoCategory> videoCategories) {
        List<String> youtubeVideoIds = IdsFetcher.getIdsFrom(youTubeVideos, com.google.api.services.youtube.model.Video::getId);
        Map<String, Video> dbVideoMap = fetchVideos(youtubeVideoIds);
        Map<String, Channel> dbChannelMap = MapCreator.toMap(dbChannels, Channel::getYouTubeChannelId);
        Map<String, VideoCategory> dbVideoCategoryMap = MapCreator.toMap(videoCategories, VideoCategory::getYoutubeId);

        return youTubeVideos
                .stream()
                .map(ytVideo -> {
                    var channelId = dbChannelMap.get(ytVideo.getSnippet().getChannelId()).getId();
                    var channel = entityManager.getReference(Channel.class, channelId);
                    var videoCategory = dbVideoCategoryMap.get(ytVideo.getSnippet().getCategoryId());
                    return createOrUpdateVideo(ytVideo, dbVideoMap, channel, videoCategory);
                })
                .collect(Collectors.toList());
    }


    private Map<String, Video> fetchVideos(List<String> youtubeVideoIds) {
        List<Video> videos = repository.findAllByYouTubeVideoIdIn(youtubeVideoIds);
        return MapCreator.toMap(videos, Video::getYouTubeVideoId);
    }


    private Video createOrUpdateVideo(com.google.api.services.youtube.model.Video youTubeVideo, Map<String, Video> dbVideoMap, Channel dbChannel, VideoCategory dbVideoCategory) {
        return Optional
                .ofNullable(dbVideoMap.get(youTubeVideo.getId()))
                .map(video -> updateVideo(video, dbChannel, dbVideoCategory))
                .orElse(createNewVideo(youTubeVideo, dbChannel, dbVideoCategory));
    }


    private Video createNewVideo(com.google.api.services.youtube.model.Video youTubeVideo, Channel dbChannel, VideoCategory dbVideoCategory) {
        return translateToVideo(youTubeVideo, dbChannel, dbVideoCategory);
    }


    private Video updateVideo(Video video, Channel channel, VideoCategory videoCategory) {
        video.setChannel(channel);
        video.setVideoCategory(videoCategory);
        video.setModifiedAt(Instant.now());
        return video;
    }


    private Video translateToVideo(com.google.api.services.youtube.model.Video youtubeVideo, Channel channel, VideoCategory videoCategory) {
        List<String> uriList = VideoDescription.toListOfUri(youtubeVideo.getSnippet().getDescription());
        return EntityCreator.createVideo(youtubeVideo.getId(), youtubeVideo.getSnippet(), youtubeVideo.getContentDetails(), uriList, channel, videoCategory);
    }
}
