package com.jlisok.youtube_activity_manager.login.dto;

import javax.validation.constraints.NotBlank;


public class GoogleLoginRequestDto {

    @NotBlank
    private final String googleIdToken;

    @NotBlank
    private final String accessToken;

    public GoogleLoginRequestDto(String googleIdToken, String accessToken) {
        this.googleIdToken = googleIdToken;
        this.accessToken = accessToken;
    }

    public String getGoogleIdToken() {
        return googleIdToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

}
