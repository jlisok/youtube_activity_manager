package com.jlisok.youtube_activity_manager.videos.models;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.vladmihalcea.hibernate.type.interval.PostgreSQLIntervalType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "videos")
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
@TypeDef(typeClass = PostgreSQLIntervalType.class, defaultForType = Duration.class)
public class Video {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "title")
    private String title;

    @Column(name = "youtube_id")
    private String youTubeId;

    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "duration", columnDefinition = "interval")
    private Duration duration;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Type(type = "list-array")
    @Column(name = "hashtag", columnDefinition = "text[]")
    private List<String> hashtag;

    @Type(type = "list-array")
    @Column(name = "uri", columnDefinition = "text[]")
    private List<String> uri;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "modified_at")
    private Instant modifiedAt;

    public Video() {
    }

    Video(UUID id, String title, String youTubeId, String channelId, Duration duration, Instant publishedAt, List<String> hashtag, List<String> uri, Instant createdAt, Instant modifiedAt) {
        this.id = id;
        this.title = title;
        this.youTubeId = youTubeId;
        this.channelId = channelId;
        this.duration = duration;
        this.publishedAt = publishedAt;
        this.hashtag = hashtag;
        this.uri = uri;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public List<String> getHashtag() {
        return hashtag;
    }

    public void setHashtag(List<String> hashtag) {
        this.hashtag = hashtag;
    }

    public List<String> getUri() {
        return uri;
    }

    public void setUri(List<String> uri) {
        this.uri = uri;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return id.equals(video.id) &&
                title.equals(video.title) &&
                channelId.equals(video.channelId) &&
                duration.equals(video.duration) &&
                publishedAt.equals(video.publishedAt) &&
                Objects.equals(hashtag, video.hashtag) &&
                Objects.equals(uri, video.uri) &&
                createdAt.equals(video.createdAt) &&
                modifiedAt.equals(video.modifiedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, channelId, duration, publishedAt, hashtag, uri, createdAt, modifiedAt);
    }
}