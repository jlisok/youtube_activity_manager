package com.jlisok.youtube_activity_manager.youtube.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.jlisok.youtube_activity_manager.videos.enums.Rating;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

public class RatingDto {

    @NotNull
    @Enumerated(EnumType.STRING)
    private final com.jlisok.youtube_activity_manager.videos.enums.Rating rating;

    @JsonCreator
    public RatingDto(com.jlisok.youtube_activity_manager.videos.enums.Rating rating) {
        this.rating = rating;
    }

    public com.jlisok.youtube_activity_manager.videos.enums.Rating getRating() {
        return rating;
    }

}
