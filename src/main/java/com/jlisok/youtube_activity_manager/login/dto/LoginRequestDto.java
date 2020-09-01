package com.jlisok.youtube_activity_manager.login.dto;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


public class LoginRequestDto {

    @NotBlank
    private final String password;

    @NotBlank
    @Email
    private final String email;


    public LoginRequestDto(String password, String email) {
        this.password = password;
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
