package com.jlisok.youtube_activity_manager.youtube.dto;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class VideoDtoBuilder {
    private UUID id;
    private String title;
    private Duration duration;
    private Instant publishedAt;
    private String channelTitle;
    private String videoCategory;

    public VideoDtoBuilder setId(UUID id) {
        this.id = id;
        return this;
    }

    public VideoDtoBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public VideoDtoBuilder setDuration(Duration duration) {
        this.duration = duration;
        return this;
    }

    public VideoDtoBuilder setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
        return this;
    }

    public VideoDtoBuilder setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
        return this;
    }

    public VideoDtoBuilder setVideoCategory(String videoCategory) {
        this.videoCategory = videoCategory;
        return this;
    }

    public VideoDto createVideoDto() {
        return new VideoDto(id, title, duration, publishedAt, channelTitle, videoCategory);
    }
}