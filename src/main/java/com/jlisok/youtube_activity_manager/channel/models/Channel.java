package com.jlisok.youtube_activity_manager.channel.models;

import com.jlisok.youtube_activity_manager.users.models.User;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "channels")
public class Channel {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "youtube_channel_id")
    private String youTubeChannelId;

    @Column(name = "title")
    private String title;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "language")
    private String language;

    @Column(name = "country")
    private String country;

    @Column(name = "owner")
    private String owner;

    @Column(name = "number_of_views")
    private Long viewNumber;

    @Column(name = "number_of_subscribers")
    private Long subscriberNumber;

    @Column(name = "number_of_videos")
    private Integer videoNumber;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "modified_at")
    private Instant modifiedAt;

    @ManyToMany(mappedBy = "channels")
    private Set<User> users = new HashSet<>();

    public Channel() {
    }

    Channel(UUID id, String youTubeChannelId, String title, Instant publishedAt, String language, String country, String owner, Long viewNumber, Long subscriberNumber, Integer videoNumber, Instant createdAt, Instant modifiedAt, Set<User> users) {
        this.id = id;
        this.youTubeChannelId = youTubeChannelId;
        this.title = title;
        this.publishedAt = publishedAt;
        this.language = language;
        this.country = country;
        this.owner = owner;
        this.viewNumber = viewNumber;
        this.subscriberNumber = subscriberNumber;
        this.videoNumber = videoNumber;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.users = users;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getYouTubeChannelId() {
        return youTubeChannelId;
    }

    public void setYouTubeChannelId(String youTubeChannelId) {
        this.youTubeChannelId = youTubeChannelId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getViewNumber() {
        return viewNumber;
    }

    public void setViewNumber(Long viewNumber) {
        this.viewNumber = viewNumber;
    }

    public Long getSubscriberNumber() {
        return subscriberNumber;
    }

    public void setSubscriberNumber(Long subscriberNumber) {
        this.subscriberNumber = subscriberNumber;
    }

    public Integer getVideoNumber() {
        return videoNumber;
    }

    public void setVideoNumber(Integer videoNumber) {
        this.videoNumber = videoNumber;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return id.equals(channel.id) &&
                youTubeChannelId.equals(channel.youTubeChannelId) &&
                title.equals(channel.title) &&
                publishedAt.equals(channel.publishedAt) &&
                Objects.equals(language, channel.language) &&
                Objects.equals(country, channel.country) &&
                Objects.equals(owner, channel.owner) &&
                viewNumber.equals(channel.viewNumber) &&
                subscriberNumber.equals(channel.subscriberNumber) &&
                videoNumber.equals(channel.videoNumber) &&
                createdAt.equals(channel.createdAt) &&
                modifiedAt.equals(channel.modifiedAt) &&
                users.equals(channel.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, youTubeChannelId, title, publishedAt, language, country, owner, viewNumber, subscriberNumber, videoNumber, createdAt, modifiedAt, users);
    }
}
