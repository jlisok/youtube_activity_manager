package com.jlisok.youtube_activity_manager.youtube.dto;

import javax.validation.constraints.NotBlank;

public class YouTubeListDto {

    @NotBlank
    private final String requestParts;

    private final String rating;

    public YouTubeListDto(String requestParts, String rating) {
        this.requestParts = requestParts;
        this.rating = rating;
    }

    public String getRequestParts() {
        return requestParts;
    }

    public String getRating() {
        return rating;
    }

}
