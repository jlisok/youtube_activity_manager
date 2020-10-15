package com.jlisok.youtube_activity_manager.youtube.dto;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class VideoDto {

    private final UUID id;
    private final String title;
    private final Duration duration;
    private final Instant publishedAt;
    private final String channelTitle;
    private final String videoCategory;

    VideoDto(UUID id, String title, Duration duration, Instant publishedAt, String channelTitle, String videoCategory) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.publishedAt = publishedAt;
        this.channelTitle = channelTitle;
        this.videoCategory = videoCategory;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Duration getDuration() {
        return duration;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public String getVideoCategory() {
        return videoCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoDto videoDto = (VideoDto) o;
        return id.equals(videoDto.id) &&
                title.equals(videoDto.title) &&
                duration.equals(videoDto.duration) &&
                publishedAt.equals(videoDto.publishedAt) &&
                channelTitle.equals(videoDto.channelTitle) &&
                videoCategory.equals(videoDto.videoCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, duration, publishedAt, channelTitle, videoCategory);
    }
}
