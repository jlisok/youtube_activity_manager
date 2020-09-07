package com.jlisok.youtube_activity_manager.login.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotBlank;


public class GoogleLoginRequestDto {

    @NotBlank
    private final String token;

    @JsonCreator
    public GoogleLoginRequestDto(String token) {
        this.token = token;
    }


    public String getToken() {
        return token;
    }

}
