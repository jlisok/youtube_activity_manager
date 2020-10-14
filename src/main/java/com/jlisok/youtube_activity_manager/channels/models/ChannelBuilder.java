package com.jlisok.youtube_activity_manager.channels.models;

import com.jlisok.youtube_activity_manager.users.models.User;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class ChannelBuilder {
    private UUID id;
    private String youTubeChannelId;
    private String title;
    private Instant publishedAt;
    private String language;
    private String country;
    private String owner;
    private Long viewNumber;
    private Long subscriberNumber;
    private Integer videoNumber;
    private Instant createdAt;
    private Instant modifiedAt;
    private Set<User> users;

    public ChannelBuilder setId(UUID id) {
        this.id = id;
        return this;
    }

    public ChannelBuilder setYouTubeChannelId(String youTubeChannelId) {
        this.youTubeChannelId = youTubeChannelId;
        return this;
    }

    public ChannelBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public ChannelBuilder setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
        return this;
    }

    public ChannelBuilder setLanguage(String language) {
        this.language = language;
        return this;
    }

    public ChannelBuilder setCountry(String country) {
        this.country = country;
        return this;
    }

    public ChannelBuilder setViewNumber(Long viewNumber) {
        this.viewNumber = viewNumber;
        return this;
    }

    public ChannelBuilder setSubscriberNumber(Long subscriberNumber) {
        this.subscriberNumber = subscriberNumber;
        return this;
    }

    public ChannelBuilder setVideoNumber(Integer videoNumber) {
        this.videoNumber = videoNumber;
        return this;
    }

    public ChannelBuilder setUsers(Set<User> users) {
        this.users = users;
        return this;
    }

    public ChannelBuilder setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public ChannelBuilder setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
        return this;
    }

    public ChannelBuilder setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Channel createChannel() {
        return new Channel(id, youTubeChannelId, title, publishedAt, language, country, owner, viewNumber, subscriberNumber, videoNumber, createdAt, modifiedAt, users);
    }
}