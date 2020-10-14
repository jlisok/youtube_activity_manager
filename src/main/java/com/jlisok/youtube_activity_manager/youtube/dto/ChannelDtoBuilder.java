package com.jlisok.youtube_activity_manager.youtube.dto;

import java.time.Instant;
import java.util.UUID;

public class ChannelDtoBuilder {
    private UUID id;
    private String title;
    private Instant publishedAt;
    private Long viewNumber;
    private Long subscriberNumber;
    private Integer videoNumber;

    public ChannelDtoBuilder setId(UUID id) {
        this.id = id;
        return this;
    }

    public ChannelDtoBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public ChannelDtoBuilder setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
        return this;
    }

    public ChannelDtoBuilder setViewNumber(Long viewNumber) {
        this.viewNumber = viewNumber;
        return this;
    }

    public ChannelDtoBuilder setSubscriberNumber(Long subscriberNumber) {
        this.subscriberNumber = subscriberNumber;
        return this;
    }

    public ChannelDtoBuilder setVideoNumber(Integer videoNumber) {
        this.videoNumber = videoNumber;
        return this;
    }

    public ChannelDto createChannelDto() {
        return new ChannelDto(id, title, publishedAt, viewNumber, subscriberNumber, videoNumber);
    }
}