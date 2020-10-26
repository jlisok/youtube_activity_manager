package com.jlisok.youtube_activity_manager.users.models;

import com.jlisok.youtube_activity_manager.channels.models.Channel;
import com.jlisok.youtube_activity_manager.userPersonalData.models.UserPersonalData;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "users")
public final class User {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "google_id_token")
    private String googleIdToken;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "google_id")
    private String googleId;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "modified_at")
    private Instant modifiedAt;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private UserPersonalData userPersonalData;

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private Set<Channel> channels = new HashSet<>();

    public User() {
    }

    // builder
    User(UUID id, String password, String email, String googleIdToken, String accessToken, String googleId, Instant createdAt, Instant modifiedAt, UserPersonalData userPersonalData, Set<Channel> channels) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.googleIdToken = googleIdToken;
        this.accessToken = accessToken;
        this.googleId = googleId;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.userPersonalData = userPersonalData;
        addChannels(channels);
    }

    public void addChannels(Collection<Channel> channels) {
        this.channels.addAll(channels);
        channels.forEach(channel -> channel.getUsers().add(this));
    }

    public void removeChannels(Collection<Channel> channels) {
        this.channels.removeAll(channels);
        channels.forEach(channel -> channel.getUsers().remove(this));
    }

    public UserPersonalData getUserPersonalData() {
        return userPersonalData;
    }

    public void setUserPersonalData(UserPersonalData userPersonalData) {
        this.userPersonalData = userPersonalData;
    }

    public String getPassword() {
        return password;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getGoogleIdToken() {
        return googleIdToken;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleIdToken(String googleIdToken) {
        this.googleIdToken = googleIdToken;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


    public Set<Channel> getChannels() {
        return channels;
    }

    public void setChannels(Set<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
