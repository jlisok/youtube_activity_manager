package com.jlisok.youtube_activity_manager.videos.models;

import com.jlisok.youtube_activity_manager.database.enums.VideoRatingEnumTypePostgreSql;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users_videos")
@TypeDef(name = "video_rating", typeClass = VideoRatingEnumTypePostgreSql.class)
public class UserVideo {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "video_id")
    private Video video;

    @Column(name = "rating")
    @Enumerated(EnumType.STRING)
    @Type(type = "video_rating")
    private Rating rating;

    @Column(name = "createdAt")
    private Instant createdAt;

    @Column(name = "modifiedAt")
    private Instant modifiedAt;

    public UserVideo() {
    }

    public UserVideo(UUID id, User user, Video video, Rating rating, Instant createdAt, Instant modifiedAt) {
        this.id = id;
        this.user = user;
        this.video = video;
        this.rating = rating;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserVideo userVideo = (UserVideo) o;
        return id.equals(userVideo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
