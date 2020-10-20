package com.jlisok.youtube_activity_manager.cloudData.dto;

import java.util.List;
import java.util.Objects;

public class YouTubeActivityCloudData {

    private final List<String> uri;
    private final List<String> hashtag;

    public YouTubeActivityCloudData(List<String> uri, List<String> hashtag) {
        this.uri = uri;
        this.hashtag = hashtag;
    }

    public List<String> getUri() {
        return uri;
    }

    public List<String> getHashtag() {
        return hashtag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YouTubeActivityCloudData that = (YouTubeActivityCloudData) o;
        return Objects.equals(uri, that.uri) &&
                Objects.equals(hashtag, that.hashtag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, hashtag);
    }
}
