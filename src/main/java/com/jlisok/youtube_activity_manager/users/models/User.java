package com.jlisok.youtube_activity_manager.users.models;

import com.jlisok.youtube_activity_manager.userPersonalData.models.UserPersonalData;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

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

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "modified_at")
    private Instant modifiedAt;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private UserPersonalData userPersonalData;

    public User() {
    }

    public User(UUID id, String password, String email, Instant createdAt, Instant modifiedAt, UserPersonalData userPersonalData) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.userPersonalData = userPersonalData;
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

    //TODO
    public UUID getGoogleId() {
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) &&
                password.equals(user.password) &&
                email.equals(user.email) &&
                createdAt.equals(user.createdAt) &&
                modifiedAt.equals(user.modifiedAt) &&
                userPersonalData.equals(user.userPersonalData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, password, email, createdAt, modifiedAt);
    }
}
