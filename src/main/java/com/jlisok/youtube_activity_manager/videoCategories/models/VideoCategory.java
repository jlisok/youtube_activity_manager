package com.jlisok.youtube_activity_manager.videoCategories.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "video_categories")
public class VideoCategory {

    @Id
    private UUID id;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "youtube_id")
    private String youtubeId;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "modified_at")
    private Instant modifiedAt;

    public VideoCategory() {
    }

    public VideoCategory(UUID id, String categoryName, String youtubeId, Instant createdAt, Instant modifiedAt) {
        this.id = id;
        this.categoryName = categoryName;
        this.youtubeId = youtubeId;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
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
        VideoCategory that = (VideoCategory) o;
        return id.equals(that.id) &&
                categoryName.equals(that.categoryName) &&
                youtubeId.equals(that.youtubeId) &&
                createdAt.equals(that.createdAt) &&
                modifiedAt.equals(that.modifiedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, categoryName, youtubeId, createdAt, modifiedAt);
    }
}
