package com.jlisok.youtube_activity_manager.youtube.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

public class YouTubeRatingDto {

    @NotNull
    @Enumerated(EnumType.STRING)
    private final Rating rating;

    @JsonCreator
    public YouTubeRatingDto(Rating rating) {
        this.rating = rating;
    }

    public Rating getRating() {
        return rating;
    }

}
