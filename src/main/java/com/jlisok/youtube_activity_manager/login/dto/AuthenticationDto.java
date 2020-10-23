package com.jlisok.youtube_activity_manager.login.dto;

import java.util.Objects;
import java.util.UUID;

public class AuthenticationDto {

    private final UUID userId;
    private final String jwtToken;

    public AuthenticationDto(UUID userId, String jwtToken) {
        this.userId = userId;
        this.jwtToken = jwtToken;
    }


    public UUID getUserId() {
        return userId;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticationDto that = (AuthenticationDto) o;
        return userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
