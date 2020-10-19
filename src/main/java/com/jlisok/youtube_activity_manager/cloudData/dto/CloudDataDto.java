package com.jlisok.youtube_activity_manager.cloudData.dto;

import java.util.List;
import java.util.Objects;

public class CloudDataDto {

    private final List<YouTubeActivityCloudData> cloudData;
    private final List<String> videoCategories;

    public CloudDataDto(List<YouTubeActivityCloudData> cloudData, List<String> videoCategories) {
        this.cloudData = cloudData;
        this.videoCategories = videoCategories;
    }

    public List<YouTubeActivityCloudData> getCloudData() {
        return cloudData;
    }

    public List<String> getVideoCategories() {
        return videoCategories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CloudDataDto dataDto = (CloudDataDto) o;
        return Objects.equals(cloudData, dataDto.cloudData) &&
                Objects.equals(videoCategories, dataDto.videoCategories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cloudData, videoCategories);
    }
}
