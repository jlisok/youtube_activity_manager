package com.jlisok.youtube_activity_manager.videos.models;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.videoCategories.models.VideoCategory;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.vladmihalcea.hibernate.type.interval.PostgreSQLIntervalType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
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

    @Column(name = "youtube_video_id")
    private String youTubeVideoId;

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

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "video_category_id")
    private VideoCategory videoCategory;

    public Video() {
    }

    public Video(UUID id, String title, String youTubeVideoId, Duration duration, Instant publishedAt, List<String> hashtag, List<String> uri, Instant createdAt, Instant modifiedAt, Channel channel, VideoCategory videoCategory) {
        this.id = id;
        this.title = title;
        this.youTubeVideoId = youTubeVideoId;
        this.duration = duration;
        this.publishedAt = publishedAt;
        this.hashtag = hashtag;
        this.uri = uri;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.channel = channel;
        this.videoCategory = videoCategory;
    }

    public VideoCategory getVideoCategory() {
        return videoCategory;
    }

    public void setVideoCategory(VideoCategory videoCategory) {
        this.videoCategory = videoCategory;
    }

    public String getYouTubeVideoId() {
        return youTubeVideoId;
    }

    public void setYouTubeVideoId(String youTubeVideoId) {
        this.youTubeVideoId = youTubeVideoId;
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

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return id.equals(video.id) &&
                title.equals(video.title) &&
                youTubeVideoId.equals(video.youTubeVideoId) &&
                duration.equals(video.duration) &&
                publishedAt.equals(video.publishedAt) &&
                Objects.equals(hashtag, video.hashtag) &&
                Objects.equals(uri, video.uri) &&
                createdAt.equals(video.createdAt) &&
                modifiedAt.equals(video.modifiedAt) &&
                channel.equals(video.channel) &&
                videoCategory.equals(video.videoCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, youTubeVideoId, duration, publishedAt, hashtag, uri, createdAt, modifiedAt, channel, videoCategory);
    }
}
