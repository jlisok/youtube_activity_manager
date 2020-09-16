package com.jlisok.youtube_activity_manager.login.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotBlank;


public class GoogleLoginRequestDto {

    @NotBlank
    private final String token;

    @NotBlank
    private final String accessToken;

    @JsonCreator
    public GoogleLoginRequestDto(String token, String accessToken) {
        this.token = token;
        this.accessToken = accessToken;
    }

    public String getToken() {
        return token;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
