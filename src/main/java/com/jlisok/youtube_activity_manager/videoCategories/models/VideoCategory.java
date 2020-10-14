package com.jlisok.youtube_activity_manager.videoCategories.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;
import java.util.UUID;

@Entity
public class VideoCategory {

    @Id
    private UUID id;

    @Column(name = "region")
    private String region;

    @Column(name = "categoryName")
    private String categoryName;

    public VideoCategory(UUID id, String region, String categoryName) {
        this.id = id;
        this.region = region;
        this.categoryName = categoryName;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoCategory that = (VideoCategory) o;
        return id.equals(that.id) &&
                region.equals(that.region) &&
                categoryName.equals(that.categoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, region, categoryName);
    }
}
