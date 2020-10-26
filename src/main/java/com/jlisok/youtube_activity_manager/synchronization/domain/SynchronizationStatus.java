package com.jlisok.youtube_activity_manager.synchronization.domain;

import com.jlisok.youtube_activity_manager.database.enums.SynchronizationStateEnumTypePostgreSql;
import com.jlisok.youtube_activity_manager.users.models.User;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "synchronization_statuses")
@TypeDef(name = "state", typeClass = SynchronizationStateEnumTypePostgreSql.class)
public class SynchronizationStatus {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    @Type(type = "state")
    private SynchronizationState state;

    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public SynchronizationStatus() {
    }

    public SynchronizationStatus(UUID id, SynchronizationState state, Instant createdAt, User user) {
        this.id = id;
        this.state = state;
        this.createdAt = createdAt;
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SynchronizationState getState() {
        return state;
    }

    public void setState(SynchronizationState state) {
        this.state = state;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SynchronizationStatus that = (SynchronizationStatus) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
