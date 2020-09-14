package com.jlisok.youtube_activity_manager.youtube.dto;

import javax.validation.constraints.NotBlank;

public class YouTubeVideoDto {

    @NotBlank
    private final String requestParts;

    @NotBlank
    private final String rating;

    public YouTubeVideoDto(@NotBlank String requestParts, String rating) {
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
