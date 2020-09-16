package com.jlisok.youtube_activity_manager.youtube.utils;

import com.google.api.services.youtube.model.VideoContentDetails;
import com.google.api.services.youtube.model.VideoSnippet;
import com.jlisok.youtube_activity_manager.videos.models.Video;
import com.jlisok.youtube_activity_manager.videos.models.VideoBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class VideoCreator {

    public static Video createVideo(String videoId, VideoSnippet snippet, VideoContentDetails details, List<String> uriList) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        return new VideoBuilder()
                .setId(id)
                .setYouTubeId(videoId)
                .setChannelId(snippet.getChannelId())
                .setHashtag(snippet.getTags())
                .setUri(uriList)
                .setDuration(Duration.parse(details.getDuration()))
                .setTitle(snippet.getTitle())
                .setPublishedAt(Instant.ofEpochMilli(snippet
                        .getPublishedAt()
                        .getValue()))
                .setCreatedAt(now)
                .setModifiedAt(now)
                .createVideo();
    }

}
