package com.jlisok.youtube_activity_manager.videos.models;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class VideoBuilder {
    private UUID id;
    private String title;
    private String youTubeId;
    private Duration duration;
    private Instant publishedAt;
    private List<String> hashtag;
    private List<String> uri;
    private Instant createdAt;
    private Instant modifiedAt;
    private Channel channel;
    private VideoCategory videoCategory;

    public VideoBuilder setId(UUID id) {
        this.id = id;
        return this;
    }

    public VideoBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public VideoBuilder setYouTubeId(String youTubeId) {
        this.youTubeId = youTubeId;
        return this;
    }

    public VideoBuilder setDuration(Duration duration) {
        this.duration = duration;
        return this;
    }

    public VideoBuilder setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
        return this;
    }

    public VideoBuilder setHashtag(List<String> hashtag) {
        this.hashtag = hashtag;
        return this;
    }

    public VideoBuilder setUri(List<String> uri) {
        this.uri = uri;
        return this;
    }

    public VideoBuilder setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public VideoBuilder setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
        return this;
    }

    public VideoBuilder setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }

    public VideoBuilder setVideoCategory(VideoCategory videoCategory) {
        this.videoCategory = videoCategory;
        return this;
    }


    public Video createVideo() {
        return new Video(id, title, youTubeId, duration, publishedAt, hashtag, uri, createdAt, modifiedAt, channel, videoCategory);
    }
}