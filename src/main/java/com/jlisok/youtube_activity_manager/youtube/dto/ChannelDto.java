package com.jlisok.youtube_activity_manager.youtube.dto;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ChannelDto {

    private final UUID id;
    private final String title;
    private final Instant publishedAt;
    private final Long viewNumber;
    private final Long subscriberNumber;
    private final Integer videoNumber;


    ChannelDto(UUID id, String title, Instant publishedAt, Long viewNumber, Long subscriberNumber, Integer videoNumber) {
        this.id = id;
        this.title = title;
        this.publishedAt = publishedAt;
        this.viewNumber = viewNumber;
        this.subscriberNumber = subscriberNumber;
        this.videoNumber = videoNumber;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public Long getViewNumber() {
        return viewNumber;
    }

    public Long getSubscriberNumber() {
        return subscriberNumber;
    }

    public Integer getVideoNumber() {
        return videoNumber;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelDto that = (ChannelDto) o;
        return id.equals(that.id) &&
                title.equals(that.title) &&
                publishedAt.equals(that.publishedAt) &&
                viewNumber.equals(that.viewNumber) &&
                subscriberNumber.equals(that.subscriberNumber) &&
                videoNumber.equals(that.videoNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, publishedAt, viewNumber, subscriberNumber, videoNumber);
    }
}
