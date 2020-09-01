package com.jlisok.youtube_activity_manager.aboutus.dto;

public class AboutUsDto {

    private final String version;
    private final String email;

    public AboutUsDto(String version, String email) {
        this.version = version;
        this.email = email;
    }


    public String getVersion() {
        return version;
    }

    public String getEmail() {
        return email;
    }

}
