package com.jlisok.youtube_activity_manager.videos.models;

import com.jlisok.youtube_activity_manager.database.enums.VideoRatingEnumTypePostgreSql;
import com.jlisok.youtube_activity_manager.users.models.User;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "users_videos")
@TypeDef(name = "video_rating", typeClass = VideoRatingEnumTypePostgreSql.class)
public class UserVideo {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "video_id")
    private Video video;

    @Column(name = "rating")
    @Enumerated(EnumType.STRING)
    @Type(type = "video_rating")
    private Rating rating;

    public UserVideo() {
    }

    public UserVideo(UUID id, User user, Video video, Rating rating) {
        this.id = id;
        this.user = user;
        this.video = video;
        this.rating = rating;
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
}
