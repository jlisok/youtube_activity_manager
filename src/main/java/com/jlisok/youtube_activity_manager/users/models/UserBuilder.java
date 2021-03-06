package com.jlisok.youtube_activity_manager.users.models;

import com.google.common.collect.Sets;
import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.userPersonalData.models.UserPersonalData;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class UserBuilder {

    private UUID id;
    private String password;
    private String email;
    private Instant createdAt;
    private Instant modifiedAt;
    private UserPersonalData userPersonalData;
    private String googleId;
    private String googleIdToken;
    private String accessToken;
    private Set<Channel> channels = Sets.newHashSet();

    public UserBuilder setId(UUID id) {
        this.id = id;
        return this;
    }

    public UserBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public UserBuilder setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
        return this;
    }

    public UserBuilder setUserPersonalData(UserPersonalData userPersonalData) {
        this.userPersonalData = userPersonalData;
        return this;
    }

    public UserBuilder setGoogleId(String googleId) {
        this.googleId = googleId;
        return this;
    }

    public UserBuilder setGoogleIdToken(String googleIdToken) {
        this.googleIdToken = googleIdToken;
        return this;
    }

    public UserBuilder setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public UserBuilder setChannels(Set<Channel> channels) {
        this.channels = channels;
        return this;
    }

    public User createUser() {
        return new User(id, password, email, googleIdToken, accessToken, googleId, createdAt, modifiedAt, userPersonalData, channels);
    }
}